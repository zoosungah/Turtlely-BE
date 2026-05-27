from fastapi import FastAPI, Depends, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import List, Optional
import math
import datetime
from sqlalchemy import create_engine, Column, Integer, Float, String, Text, DateTime, func
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from apscheduler.schedulers.background import BackgroundScheduler
import contextlib

# 데이터베이스 설정
DATABASE_URL = "sqlite:///./turtly_vision.db"
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()


class VisionReport(Base):
    __tablename__ = "vision_reports"
    
    id = Column(Integer, primary_key=True, index=True)
    login_id = Column(String(100), nullable=False)
    nickname = Column(String(50), nullable=False)  
    cva_angle = Column(Float, nullable=False)
    cra_angle = Column(Float, nullable=False)
    posture_status = Column(String(20), nullable=False)
    message = Column(Text)
    created_at = Column(DateTime, default=datetime.datetime.now)

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# 데이터 구조 (MediaPipe 0.0 ~ 1.0 정규화 비율 기준)
class FrameData(BaseModel):
    eye_x: float
    eye_y: float
    tragus_x: float
    tragus_y: float
    c7_x: float
    c7_y: float

class AnalyzeRequest(BaseModel):
    login_id: str  
    nickname: str  
    frames: List[FrameData]

    class Config:
        json_schema_extra = {
            "example": {
                "login_id": "sykim0215@gmail.com",
                "nickname": "승연",
                "frames": [
                    {
                        "eye_x": 0.45, "eye_y": 0.35,
                        "tragus_x": 0.51, "tragus_y": 0.38,
                        "c7_x": 0.50, "c7_y": 0.55
                    },
                    {
                        "eye_x": 0.45, "eye_y": 0.35,
                        "tragus_x": 0.52, "tragus_y": 0.38,
                        "c7_x": 0.50, "c7_y": 0.56
                    }
                ]
            }
        }

#  월간 리포트 응답 구조 
class MonthlyReportResponse(BaseModel):
    status: int  
    message: str
    year: int
    month: int
    nickname: str                           
    posture_status: str                    
    posture_message: Optional[str] = None  
    cva_angle: Optional[float] = None       
    cra_angle: Optional[float] = None       
    total_measurements: int  

# 30일 주기 체크 스케줄러 로직
def check_30day_remeasure():
    db: Session = SessionLocal()
    try:
        today = datetime.datetime.now().date()
        target_date = today - datetime.timedelta(days=30)
        
        subquery = db.query(
            VisionReport.login_id,
            func.max(VisionReport.created_at).label('last_measurement')
        ).group_by(VisionReport.login_id).subquery()
        
        users_to_notify = db.query(subquery.c.login_id).filter(
            func.date(subquery.c.last_measurement) == target_date
        ).all()
        
        for user in users_to_notify:
            login_id = user.login_id
            print(f"[백엔드 자동 알림] 대상자: {login_id} - '오늘은 정기 재측정 날입니다!'")

    except Exception as e:
        print(f"스케줄러 에러 발생: {e}")
    finally:
        db.close()

@contextlib.asynccontextmanager
async def lifespan(app: FastAPI):
    Base.metadata.create_all(bind=engine)
    scheduler = BackgroundScheduler()
    scheduler.add_job(check_30day_remeasure, 'cron', hour=9, minute=0)
    scheduler.start()
    print("30일 정기 재측정 자동 알림 스케줄러가 활성화되었습니다. (매일 오전 9시 작동)")
    
    yield  
    scheduler.shutdown()

app = FastAPI(lifespan=lifespan)

# CORS 설정 
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"], 
    allow_headers=["*"], 
)

@app.get("/")
def read_root():
    return {"message": "Turtly Vision AI Server is Running!"}

@app.post("/report/analyze")
async def analyze_posture(data: AnalyzeRequest, db: Session = Depends(get_db)):
    try:
        if not data.frames:
            return {"status": "error", "message": "전송된 프레임 데이터가 없습니다."}

        best_frame = None
        min_score = float('inf')

        for i, current in enumerate(data.frames):
            current_coords = [current.eye_x, current.eye_y, current.tragus_x, current.tragus_y, current.c7_x, current.c7_y]
            if any(c <= 0 or c > 1.0 for c in current_coords):
                continue
            
            if i == 0:
                movement = 0.0
            else:
                prev = data.frames[i-1]
                movement = math.sqrt(
                    (current.tragus_x - prev.tragus_x)**2 + 
                    (current.tragus_y - prev.tragus_y)**2 +
                    (current.c7_x - prev.c7_x)**2 +
                    (current.c7_y - prev.c7_y)**2
                )
            
            center_distance = math.sqrt((current.tragus_x - 0.5)**2 + (current.tragus_y - 0.5)**2)
            score = (movement * 0.7) + (center_distance * 0.3)
            
            if score < min_score:
                min_score = score
                best_frame = current

        if not best_frame:
            return {
                "status": "error",
                "message": "머리카락이나 조명으로 인해 목의 랜드마크를 확실하게 찾을 수 없습니다. 장애물을 제거하고 밝은 곳에서 다시 촬영해 주세요.",
                "detail": "No valid frames found after filtering"
            }

        # CVA 계산
        delta_y_cva = abs(best_frame.c7_y - best_frame.tragus_y)
        delta_x_cva = abs(best_frame.c7_x - best_frame.tragus_x)
        cva_angle = math.degrees(math.atan2(delta_y_cva, delta_x_cva))

        # CRA 계산
        v1 = (best_frame.eye_x - best_frame.tragus_x, best_frame.eye_y - best_frame.tragus_y)
        v2 = (best_frame.c7_x - best_frame.tragus_x, best_frame.c7_y - best_frame.tragus_y)

        dot_prod = v1[0] * v2[0] + v1[1] * v2[1]
        mag1 = math.sqrt(v1[0]**2 + v1[1]**2)
        mag2 = math.sqrt(v2[0]**2 + v2[1]**2)

        if mag1 * mag2 != 0:
            cos_theta = dot_prod / (mag1 * mag2)
            cos_theta = max(-1.0, min(1.0, cos_theta))
            cra_angle = math.degrees(math.acos(cos_theta))
        else:
            cra_angle = 0.0

        if cva_angle >= 50:
            status = "정상"
            msg = "정상입니다."
        elif 45 <= cva_angle < 50:
            status = "일자목"
            if cra_angle > 155:
                msg = "일자목 단계입니다."
            else:
                msg = "경추의 C자 곡선이 펴지고 있습니다. 틈틈이 스트레칭을 해주세요."
        elif 40 <= cva_angle < 45:
            status = "거북목"
            msg = "거북목 상태입니다."
        else:
            status = "역C자목"
            msg = "경추 정렬이 반대로 변형된 위험한 상태입니다. 전문적인 교정과 진단을 권장합니다."

        # 유저 진짜 닉네임 매핑하여 DB 저장
        new_report = VisionReport(
            login_id=data.login_id,
            nickname=data.nickname,  
            cva_angle=round(cva_angle, 2),
            cra_angle=round(cra_angle, 2),
            posture_status=status,
            message=msg
        )
        db.add(new_report)
        db.commit()
        db.refresh(new_report)

        return {
            "status": "success",
            "message": "최적의 프레임 분석 및 결과 저장이 성공적으로 완료되었습니다.",
            "data": {
                "report_id": new_report.id,
                "posture_status": new_report.posture_status,
                "created_at": new_report.created_at
            }
        }

    except Exception as e:
        db.rollback() 
        return {
            "status": "error",
            "message": "분석 중 오류가 발생했습니다.",
            "detail": str(e)
        }


@app.get("/report/monthly", response_model=MonthlyReportResponse)
def get_monthly_report(login_id: str, year: int, month: int, db: Session = Depends(get_db)):
    try:
        start_date = datetime.datetime(year, month, 1)
        if month == 12:
            end_date = datetime.datetime(year + 1, 1, 1)
        else:
            end_date = datetime.datetime(year, month + 1, 1)

        # 해당 년/월 범위 데이터 최신순 정렬하여 조회
        try:
            monthly_reports = db.query(VisionReport).filter(
                VisionReport.login_id == login_id,
                VisionReport.created_at >= start_date,
                VisionReport.created_at < end_date
            ).order_by(VisionReport.created_at.desc()).all()
        except Exception as db_err:
            return JSONResponse(
                status_code=500,
                content={
                    "errorCode": "DATABASE_ERROR",
                    "message": f"데이터베이스 조회 중 오류가 발생했습니다: {str(db_err)}"
                }
            )

        # 해당 월에 정기 측정 데이터가 없을 때
        if not monthly_reports:
            last_any_report = db.query(VisionReport).filter(VisionReport.login_id == login_id).first()
            user_nickname = last_any_report.nickname if last_any_report else "회원"

            return {
                "status": 200,  
                "message": f"해당 월({year}년 {month}월)의 정기 측정 기록이 존재하지 않습니다.",
                "year": year,
                "month": month,
                "nickname": user_nickname,         
                "posture_status": "데이터 없음",
                "posture_message": "이번 달 측정 기록이 존재하지 않습니다. 검사를 진행해 주세요.", 
                "cva_angle": None,                 
                "cra_angle": None,                 
                "total_measurements": 0
            }

        # 그 달에 존재하는 가장 최신(대표) 측정 데이터 1개 추출
        report = monthly_reports[0]

        # 데이터가 정상적으로 존재할 때
        return {
            "status": 200,  
            "message": f"{year}년 {month}월 정기 검사 리포트 조회가 완료되었습니다.",
            "year": year,
            "month": month,
            "nickname": report.nickname,           
            "posture_status": report.posture_status, 
            "posture_message": report.message,       
            "cva_angle": report.cva_angle,
            "cra_angle": report.cra_angle,
            "total_measurements": 1 
        }

    except Exception as e:
        return JSONResponse(
            status_code=500,
            content={
                "errorCode": "SERVER_INTERNAL_ERROR",
                "message": f"월간 리포트 가공 중 서버 내부 오류가 발생했습니다: {str(e)}"
            }
        )
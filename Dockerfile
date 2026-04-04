# 1. 자바 21 버전을 실행할 수 있는 환경을 가져옴
FROM amazoncorretto:21-alpine

# 2. 빌드된 결과물(jar 파일)의 위치를 지정
ARG JAR_FILE=build/libs/*.jar

# 3. 프로젝트의 jar 파일을 상자 안으로 복사하고 이름을 app.jar로 바꿈
COPY ${JAR_FILE} app.jar

# 4. 상자가 시작될 때 자바 프로그램을 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
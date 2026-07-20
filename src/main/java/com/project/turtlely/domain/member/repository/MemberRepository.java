package com.project.turtlely.domain.member.repository;

import com.project.turtlely.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 일반 회원가입 시 ID 중복 확인
    boolean existsByLoginId(String loginId);
  
    //아이디로 회원정보 통째로 가져오기
    Optional<Member> findByLoginId(String loginId);

    // 구글 회원가입 시 소셜 ID 중복 확인
    boolean existsBySocialId(String socialId);

    // 휴대폰 번호로 가입된 유저가 있는지 확인
    boolean existsByPhoneNumber(String phoneNumber);

    // 전화번호로 회원 찾기
    Optional<Member> findByPhoneNumber(String phoneNumber);

    // 측정 알림 신청 이후 한 달(30일)이 지난 유저 리스트 조회
    List<Member> findByIsMeasurementAlarmTrueAndMeasurementAlarmSetAtBefore(LocalDateTime dateTime);

    // 리포트 발행 알림 신청 이후 한 달(30일)이 지난 유저 리스트 조회
    List<Member> findByIsReportAlarmTrueAndReportAlarmSetAtBefore(LocalDateTime dateTime);
}
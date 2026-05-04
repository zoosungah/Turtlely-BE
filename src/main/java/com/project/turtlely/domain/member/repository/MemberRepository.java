package com.project.turtlely.domain.member.repository;

import com.project.turtlely.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // ID 중복 확인
    boolean existsByLoginId(String loginId);
}
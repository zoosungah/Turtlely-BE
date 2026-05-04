package com.project.turtlely.domain.signup.entity;

import com.project.turtlely.domain.signup.enums.Role;
import com.project.turtlely.domain.signup.enums.SocialType;
import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 50)
    private String loginId;

    private String password;

    @Column(length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // LOCAL, GOOGLE

    private String phoneNumber;
    private String socialId;

    @Enumerated(EnumType.STRING)
    private Role role; // USER, ADMIN
}

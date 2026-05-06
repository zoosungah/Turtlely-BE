package com.project.turtlely.domain.member.entity;

import com.project.turtlely.domain.member.enums.Role;
import com.project.turtlely.domain.member.enums.SocialType;
import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
    private SocialType socialType;

    private String phoneNumber;
    private String socialId;

    @Enumerated(EnumType.STRING)
    private Role role;
}

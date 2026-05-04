package com.project.turtlely.domain.member.entity;

import com.project.turtlely.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settings extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean isPushAgreed = true;
    private boolean isBluetoothAgreed = false;
    private boolean isCameraAgreed = false;
    private boolean isVibrationEnabled = true;
}

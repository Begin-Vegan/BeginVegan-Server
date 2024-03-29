package com.beginvegan.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;

import com.beginvegan.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import lombok.Builder;
import lombok.Getter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imageUrl;

    private String nickname;

    @Email
    private String email;

    private String password;

    private Boolean emailVerified = false; // emailVerified / marketingConsent는 불필요시 삭제

    private Boolean marketingConsent;

    @Enumerated(EnumType.STRING)
    private VeganType veganType;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String providerId;

    private Integer point;

    private Boolean alarmSetting;

    private String userCode;

    @Builder
    public User(Long id, String name, String imageUrl, String nickname, String email, String password, Boolean emailVerified, Boolean marketingConsent, VeganType veganType, Provider provider, Role role, String providerId, Integer point, Boolean alarmSetting, String userCode) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.emailVerified = emailVerified;
        this.marketingConsent = marketingConsent;
        this.veganType = veganType;
        this.provider = provider;
        this.role = role;
        this.providerId = providerId;
        this.point = point;
        this.alarmSetting = alarmSetting;
        this.userCode = userCode;
    }

    public void updateName(String name){
        this.name = name;
    }

    public void updateImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void updateVeganType(VeganType veganType) {
        this.veganType = veganType;
    }

    public void updateMarketingConsent(Boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

}

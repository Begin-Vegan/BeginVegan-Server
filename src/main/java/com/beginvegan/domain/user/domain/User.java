package com.beginvegan.domain.user.domain;

import jakarta.persistence.*;
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

    private String imageUrl;

    private String nickname;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private VeganType veganType;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String providerId;

    private Integer point;

    private Boolean alarmSetting = true;

    private String userCode;


    private Boolean veganTestCompleted = false;

    private Boolean customProfileCompleted = false;

    // 추가정보 입력 여부
    private Boolean signUpCompleted = false;

    @Builder
    public User(Long id, String imageUrl, String nickname, String email, String password, VeganType veganType, Provider provider, Role role, String providerId, Integer point, Boolean alarmSetting, String userCode, Boolean veganTestCompleted, Boolean customProfileCompleted) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.veganType = veganType;
        this.provider = provider;
        this.role = role;
        this.providerId = providerId;
        this.point = point;
        this.alarmSetting = alarmSetting;
        this.userCode = userCode;
        this.veganTestCompleted = veganTestCompleted;
        this.customProfileCompleted = customProfileCompleted;
    }

    // Description : 회원가입 시에만 사용할 것
    public void updateUser(String imageUrl, String nickname, String userCode, VeganType veganType, Provider provider) {
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.userCode = userCode;
        this.veganType = veganType;
        this.provider = provider;
        this.point = 0;
        this.alarmSetting = true;
        this.veganTestCompleted = false;
        this.customProfileCompleted = false;
        this.signUpCompleted = false;
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void updateUserCode(String userCode){
        this.userCode = userCode;
    }

    public void updateImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void updateSignUpCompleted(Boolean signUpCompleted){
        this.signUpCompleted = signUpCompleted;
    }

    public void updateVeganType(VeganType veganType) {
        this.veganType = veganType;
    }

    public void updateAlarmSetting(Boolean alarmSetting) {
       this.alarmSetting = alarmSetting;
    }

    public void updateVeganTestCompleted(Boolean veganTestCompleted) { this.veganTestCompleted = veganTestCompleted; }

    public void updateCustomProfileCompleted(Boolean customProfileCompleted) { this.customProfileCompleted = customProfileCompleted; }

    // Description : 해당 함수 호출 시 더해야 하는 포인트 값만 요청
    public void updatePoint(Integer additionalPoint) { this.point += additionalPoint; }

    public void subPoint(Integer point) { this.point -= point; }

}

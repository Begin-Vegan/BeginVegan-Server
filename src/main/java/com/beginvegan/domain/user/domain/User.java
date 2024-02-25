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

    @Email
    private String email;

    private String imageUrl;

    private Boolean emailVerified = false;

    private String password;

    private Boolean marketingConsent;

    @Enumerated(EnumType.STRING)
    private VeganType veganType;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String providerId;

    @Builder
    public User(Long id, String name, String email, String imageUrl, Boolean emailVerified, String password, Boolean marketingConsent, VeganType veganType, Provider provider, Role role, String providerId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.emailVerified = emailVerified;
        this.password = password;
        this.marketingConsent = marketingConsent;
        this.veganType = veganType;
        this.provider = provider;
        this.role = role;
        this.providerId = providerId;
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

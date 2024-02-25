package com.beginvegan.domain.user.dto;

import com.beginvegan.domain.user.domain.Provider;
import com.beginvegan.domain.user.domain.Role;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.VeganType;
import lombok.Builder;
import lombok.Data;

@Data
public class UserDetailRes {

    private Long id;
    private String name;
    private String email;
    private String imageUrl;
    private Boolean marketingConsent;
    private VeganType veganType;
    private Provider provider;
    private Role role;
    private String providerId;

    @Builder
    public UserDetailRes(Long id, String name, String email, String imageUrl, Boolean marketingConsent, VeganType veganType, Provider provider, Role role, String providerId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.marketingConsent = marketingConsent;
        this.veganType = veganType;
        this.provider = provider;
        this.role = role;
        this.providerId = providerId;
    }

    public static UserDetailRes toDto(User user) {
        return UserDetailRes.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .veganType(user.getVeganType())
                .role(user.getRole())
                .build();
    }

}

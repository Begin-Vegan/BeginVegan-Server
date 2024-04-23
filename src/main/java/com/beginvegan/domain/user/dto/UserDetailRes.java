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
    private String nickname;
    private String email;
    private String imageUrl;
    private VeganType veganType;
    private Provider provider;
    private Role role;
    private String providerId;

    @Builder
    public UserDetailRes(Long id, String nickname, String email, String imageUrl, VeganType veganType, Provider provider, Role role, String providerId) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.imageUrl = imageUrl;
        this.veganType = veganType;
        this.provider = provider;
        this.role = role;
        this.providerId = providerId;
    }

    public static UserDetailRes toDto(User user) {
        return UserDetailRes.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .veganType(user.getVeganType())
                .role(user.getRole())
                .build();
    }

}

package com.beginvegan.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserRestaurantDetailRes {

    private Long userId;

    private String imageUrl;

    private String nickname;

    private String userCode;

    private Integer point;

    @Builder
    public UserRestaurantDetailRes(Long userId, String imageUrl, String nickname, String userCode, Integer point) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.userCode = userCode;
        this.point = point;
    }
}

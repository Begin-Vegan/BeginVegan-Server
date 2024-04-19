package com.beginvegan.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserHomeInfoRes {

    private String nickname;

    private String userLevel;  // user의 point로 등급 조회

    @Builder
    public UserHomeInfoRes(String nickname, String userLevel) {
        this.nickname = nickname;
        this.userLevel = userLevel;
    }
}

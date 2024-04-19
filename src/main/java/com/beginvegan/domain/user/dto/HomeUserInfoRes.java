package com.beginvegan.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HomeUserInfoRes {

    @Schema(type = "string", example = "역북동불주먹", description = "변경할 유저의 닉네임 입니다.")
    private String nickname;

    @Schema(type = "string", example = "SEED, ROOT, SPROUT, STEM, LEAF, TREE, FLOWER, FRUIT", description = "유저의 사용자 레벨입니다.")
    private String userLevel;  // user의 point로 등급 조회

    @Builder
    public HomeUserInfoRes(String nickname, String userLevel) {
        this.nickname = nickname;
        this.userLevel = userLevel;
    }
}

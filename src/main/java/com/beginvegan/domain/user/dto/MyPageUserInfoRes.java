package com.beginvegan.domain.user.dto;

import com.beginvegan.domain.user.domain.VeganType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageUserInfoRes {

    private Long id;

    @Schema(type = "string", example = "https://amazonaws.com/40742637-e974-44a5-ab43-36bd9a14dcfb.png", description = "변경할 유저의 닉네임 입니다.")
    private String imageUrl;

    @Schema(type = "string", example = "역북동불주먹", description = "변경할 유저의 닉네임 입니다.")
    private String nickname;

    @Schema(type = "string", example = "SEED, ROOT, SPROUT, STEM, LEAF, TREE, FLOWER, FRUIT", description = "유저의 사용자 레벨입니다.")
    private String userLevel;

    @Schema(type = "string", example = "UNKNOWN, VEGAN, LACTO_VEGETARIAN, OVO_VEGETARIAN, LACTO_OVO_VEGETARIAN, POLLOTARIAN, PASCATARIAN, FLEXITARIAN", description = "유저의 비건 타입을 변경합니다.")
    private VeganType veganType;

    @Schema(type = "Integer", example = "0", description = "유저의 관심도 수치입니다.")
    private Integer point;

    @Builder
    public MyPageUserInfoRes(Long id, String imageUrl, String nickname, String userLevel, VeganType veganType, Integer point) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.userLevel = userLevel;
        this.veganType = veganType;
        this.point = point;
    }
}

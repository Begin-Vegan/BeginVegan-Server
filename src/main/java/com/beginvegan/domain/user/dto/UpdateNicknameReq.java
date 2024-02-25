package com.beginvegan.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateNicknameReq {

    @Schema(type = "string", example = "역북동불주먹", description = "변경할 유저의 닉네임 입니다.")
    private String nickname;

}

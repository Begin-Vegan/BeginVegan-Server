package com.beginvegan.domain.user.dto;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
public class UpdateFcmTokenReq {

    @Schema(type = "String", example = "c8z22dyWSxqH_e7Gk..", description = "Fcm Token 입니다.")
    private String fcmToken;

}

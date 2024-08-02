package com.beginvegan.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateFcmTokenReq {

    @Schema(type = "String", example = "c8z22dyWSxqH_e7Gk..", description = "Fcm Token 입니다.")
    private String fcmToken;
}

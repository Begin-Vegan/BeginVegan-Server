package com.beginvegan.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
public class StoredFcmTokenRes {

    @Schema(type = "boolean", example = "true", description = "유저의 fcm 토큰 저장 여부입니다.")
    private Boolean storedFcmToken;

    @Builder
    public StoredFcmTokenRes(Boolean storedFcmToken) { this.storedFcmToken = storedFcmToken; }
}

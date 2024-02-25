package com.beginvegan.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateMarketingConsentReq {

    @Schema(type = "boolean", example = "true/false", description = "유저의 알림 설정 여부입니다.")
    private Boolean marketingConsent;

}

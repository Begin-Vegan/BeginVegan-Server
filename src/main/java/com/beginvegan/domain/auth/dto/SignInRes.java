package com.beginvegan.domain.auth.dto;

import com.beginvegan.global.config.security.OAuth2Config;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInRes {

    private AuthRes authRes;

    @Schema( type = "boolean", example ="true", description="추가정보 기입 여부입니다.")
    private boolean signUpCompleted;

}

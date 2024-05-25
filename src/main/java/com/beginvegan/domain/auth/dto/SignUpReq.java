package com.beginvegan.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class SignUpReq {

    @Schema( type = "string", example = "string@aa.bb", description="계정 이메일 입니다.")
    @Email
    private String email;

    @Schema( type = "string", example = "string", description="소셜 로그인한 사용자의 고유 id 입니다.")
    private String providerId;
}

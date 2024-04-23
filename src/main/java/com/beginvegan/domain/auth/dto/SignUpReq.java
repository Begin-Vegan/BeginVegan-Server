package com.beginvegan.domain.auth.dto;

import com.beginvegan.domain.user.domain.VeganType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import jakarta.validation.constraints.Email;

@Data
public class SignUpReq {

    @Schema( type = "string", example = "string", description="닉네임 입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,12}$")
    private String nickname;

    @Schema( type = "string", example = "UNKNOWN, VEGAN, LACTO_VEGETARIAN, OVO_VEGETARIAN, LACTO_OVO_VEGETARIAN, POLLOTARIAN, PASCATARIAN, FLEXITARIAN", description = "비건 타입입니다.")
    private VeganType veganType;

}

package com.beginvegan.domain.user.dto;

import com.beginvegan.domain.user.domain.VeganType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateVeganTypeReq {

    @Schema(type = "string", example = "VEGAN, LACTO_VEGETARIAN, OVO_VEGETARIAN, LACTO_OVO_VEGETARIAN, POLLOTARIAN, PASCATARIAN, FLEXITARIAN", description = "유저의 비건 타입을 변경합니다.")
    private VeganType veganType;

}

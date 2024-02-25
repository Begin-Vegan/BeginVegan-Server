package com.beginvegan.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PostReviewReq {

    @Schema(type = "int", example = "1", description = "식당/카페 ID 입니다.")
    private Long restaurantId;
    @Schema(type = "string", example = "규원, 민서가 인정한 비건 식당이에요 어때 맛있지?", description = "리뷰 내용입니다.")
    private String content;

}

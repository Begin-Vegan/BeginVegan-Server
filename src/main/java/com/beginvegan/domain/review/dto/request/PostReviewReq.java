package com.beginvegan.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PostReviewReq {

    @Schema(type = "Long", example = "1", description = "식당 ID 입니다.")
    private Long restaurantId;

    @Schema(type = "Double", example = "5.0", description = "식당의 별점입니다.")
    private Double rate;

    @Schema(type = "string", example = "규원, 민서가 인정한 비건 식당이에요 어때 맛있지?", description = "리뷰 내용입니다.")
    private String content;

}

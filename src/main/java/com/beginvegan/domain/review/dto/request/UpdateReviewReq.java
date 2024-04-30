package com.beginvegan.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateReviewReq {

    @Schema(type = "Long", example = "1", description = "식당 ID 입니다.")
    private Long restaurantId;

    @Schema(type = "Double", example = "5.0", description = "식당의 별점입니다.")
    @Min(value = 0, message = "평점은 최소 0이상이어야 합니다.")
    @Max(value = 5, message = "평점은 최대 5.0까지 가능합니다.")
    private Double rate;

    @Schema(type = "string", example = "규원, 민서가 인정한 비건 식당이에요 어때 맛있지?", description = "리뷰 내용입니다.")
    @Size(min = 5, message = "최소 글자 수는 5자입니다.")
    @Size(max = 500, message = "최대 글자 수는 500자입니다.")
    private String content;
}

package com.beginvegan.domain.review.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendationByUserAndReviewRes {

    private int recommendationCount; // 추천 수

    private boolean isRecommendation;

    @Builder
    public RecommendationByUserAndReviewRes(Integer recommendationCount, boolean isRecommendation) {
        this.recommendationCount = recommendationCount;
        this.isRecommendation = isRecommendation;
    }
}

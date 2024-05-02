package com.beginvegan.domain.review.dto.response;

import com.beginvegan.domain.image.domain.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class MyReviewRes {

    private Long reviewId;

    private String restaurantName;

    private LocalDate date;

    private Double rate;

    private List<Image> images;

    private String content;

    private int countRecommendation;

    private boolean isRecommendation;

    @Builder
    public MyReviewRes(Long reviewId, String restaurantName, LocalDate date, Double rate, List<Image> images, String content, int countRecommendation, boolean isRecommendation) {
        this.reviewId = reviewId;
        this.restaurantName = restaurantName;
        this.date = date;
        this.rate = rate;
        this.images = images;
        this.content = content;
        this.countRecommendation = countRecommendation;
        this.isRecommendation = isRecommendation;
    }
}

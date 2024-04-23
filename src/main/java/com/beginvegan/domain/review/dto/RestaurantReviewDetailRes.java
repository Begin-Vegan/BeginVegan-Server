package com.beginvegan.domain.review.dto;

import com.beginvegan.domain.review.domain.ReviewType;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.dto.UserRestaurantDetailRes;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class RestaurantReviewDetailRes {

    private Long reviewId;

    private UserRestaurantDetailRes user;

    private ReviewType reviewType; // NORMAL("NORMAL"), PHOTO("PHOTO"), REPORT("REPORT");

    private List<String> imageUrl = new ArrayList<>();

    private Double rate;

    private LocalDate date; // 최종 수정일

    private String content;

    private boolean visible; // 관리자가 삭제 시 내용 안보이게 하기 위함

    private int recommendationCount; // 추천 수

    private boolean isRecommendation; // 조회 유저의 추천 여부

    @Builder
    public RestaurantReviewDetailRes(Long reviewId, UserRestaurantDetailRes user, ReviewType reviewType, List<String> imageUrl, Double rate, LocalDate date, String content, boolean visible, int recommendationCount, boolean isRecommendation) {
        this.reviewId = reviewId;
        this.user = user;
        this.reviewType = reviewType;
        this.imageUrl = imageUrl;
        this.rate = rate;
        this.date = date;
        this.content = content;
        this.visible = visible;
        this.recommendationCount = recommendationCount;
        this.isRecommendation = isRecommendation;
    }
}

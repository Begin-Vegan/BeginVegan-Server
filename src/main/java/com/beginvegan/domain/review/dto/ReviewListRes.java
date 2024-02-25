package com.beginvegan.domain.review.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ReviewListRes {

    private List<?> reviews;
    private Long totalCount;

    @Builder
    public ReviewListRes(List<?> reviews, Long totalCount) {
        this.reviews = reviews;
        this.totalCount = totalCount;
    }

}

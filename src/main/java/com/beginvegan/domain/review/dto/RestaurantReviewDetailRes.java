package com.beginvegan.domain.review.dto;

import com.beginvegan.domain.user.domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RestaurantReviewDetailRes {

    private Long id;
    private String content;
    private LocalDate date;
    private User user;

    @Builder
    public RestaurantReviewDetailRes(Long id, String content, LocalDate date, User user) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.user = user;
    }

}

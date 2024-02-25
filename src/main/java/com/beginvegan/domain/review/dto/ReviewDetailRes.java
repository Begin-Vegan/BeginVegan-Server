package com.beginvegan.domain.review.dto;

import com.beginvegan.domain.restaurant.dto.RestaurantDetailRes;
import com.beginvegan.domain.user.dto.UserDetailRes;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReviewDetailRes {

    private Long id;
    private String content;
    private LocalDate date;
    private RestaurantDetailRes restaurant;
    private UserDetailRes user;

    @Builder
    public ReviewDetailRes(Long id, String content, LocalDate date, RestaurantDetailRes restaurantDetailRes, UserDetailRes userDetailRes) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.restaurant = restaurantDetailRes;
        this.user = userDetailRes;
    }

}

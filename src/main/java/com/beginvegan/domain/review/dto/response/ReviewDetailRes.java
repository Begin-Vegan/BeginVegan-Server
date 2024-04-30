package com.beginvegan.domain.review.dto.response;

import com.beginvegan.domain.image.domain.Image;
import com.beginvegan.domain.restaurant.dto.response.RestaurantDetailRes;
import com.beginvegan.domain.user.dto.UserDetailRes;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReviewDetailRes {

    private Long id;
    private String content;
    private Double rate;
    private RestaurantInfoRes restaurant;
    private List<Image> images;

    @Builder
    public ReviewDetailRes(Long id, String content, Double rate, RestaurantInfoRes restaurantInfoRes, List<Image> images) {
        this.id = id;
        this.content = content;
        this.rate = rate;
        this.restaurant = restaurantInfoRes;
        this.images = images;
    }

}

package com.beginvegan.domain.restaurant.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class RandomRestaurantRes {

    private Long restaurantId;

    private String thumbnail;

    private String name;

    private boolean isBookmark;

    private String latitude;

    private String longitude;

    @Builder
    public RandomRestaurantRes(Long restaurantId, String thumbnail, String name, boolean isBookmark, String latitude, String longitude) {
        this.restaurantId = restaurantId;
        this.thumbnail = thumbnail;
        this.name = name;
        this.isBookmark = isBookmark;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

package com.beginvegan.domain.restaurant.dto.response;

import com.beginvegan.domain.restaurant.domain.RestaurantType;
import lombok.Builder;
import lombok.Data;

@Data
public class RestaurantBannerRes {

    private Long restaurantId;

    private String restaurantName;

    private RestaurantType restaurantType;

    private Double distance;

    private Double rate;

    private String thumbnail;

    @Builder
    public RestaurantBannerRes(Long restaurantId, String restaurantName, RestaurantType restaurantType, Double distance, Double rate, String thumbnail) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantType = restaurantType;
        this.distance = distance;
        this.rate = rate;
        this.thumbnail = thumbnail;
    }
}

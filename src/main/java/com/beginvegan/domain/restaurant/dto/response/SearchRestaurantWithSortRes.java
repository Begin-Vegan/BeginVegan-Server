package com.beginvegan.domain.restaurant.dto.response;

import com.beginvegan.domain.restaurant.domain.RestaurantType;
import lombok.Builder;
import lombok.Data;

@Data
public class SearchRestaurantWithSortRes {

    private Long restaurantId;

    private String thumbnail;

    private String name;

    private RestaurantType restaurantType;

    private Double distance;

    private Double rate;

    private String latitude;

    private String longitude;

    @Builder
    public SearchRestaurantWithSortRes(Long restaurantId, String thumbnail, String name, RestaurantType restaurantType, Double distance, Double rate, String latitude, String longitude) {
        this.restaurantId = restaurantId;
        this.thumbnail = thumbnail;
        this.name = name;
        this.restaurantType = restaurantType;
        this.distance = distance;
        this.rate = rate;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

package com.beginvegan.domain.restaurant.dto.response;

import com.beginvegan.domain.restaurant.domain.Address;
import com.beginvegan.domain.restaurant.domain.RestaurantType;
import lombok.Builder;
import lombok.Data;

@Data
public class BookmarkRestaurantRes {

    private Long restaurantId;

    private String thumbnail;

    private String name;

    private RestaurantType restaurantType;

    private Double rate;

    private Double distance;

    @Builder
    public BookmarkRestaurantRes(Long restaurantId, String thumbnail, String name, RestaurantType restaurantType, Double rate, Double distance) {
        this.restaurantId = restaurantId;
        this.thumbnail = thumbnail;
        this.name = name;
        this.restaurantType = restaurantType;
        this.rate = rate;
        this.distance = distance;
    }
}

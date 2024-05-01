package com.beginvegan.domain.review.dto.response;

import com.beginvegan.domain.restaurant.domain.Address;
import com.beginvegan.domain.restaurant.domain.RestaurantType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantInfoRes {
    private String name;

    private RestaurantType restaurantType;

    private Address address;

    @Builder
    public RestaurantInfoRes(String name, RestaurantType restaurantType, Address address) {
        this.name = name;
        this.restaurantType = restaurantType;
        this.address = address;
    }
}

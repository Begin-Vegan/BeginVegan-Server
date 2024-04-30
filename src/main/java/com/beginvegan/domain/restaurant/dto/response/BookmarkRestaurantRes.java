package com.beginvegan.domain.restaurant.dto.response;

import com.beginvegan.domain.restaurant.domain.Address;
import com.beginvegan.domain.restaurant.domain.RestaurantType;
import lombok.Builder;
import lombok.Data;

@Data
public class BookmarkRestaurantRes {

    // 썸네일, 식당 이름, 카테고리명(RestaurantType), 도로명 주소

    private Long restaurantId;

    private String thumbnail;

    private String name;

    private RestaurantType restaurantType;

    private Address address;

    @Builder
    public BookmarkRestaurantRes(Long restaurantId, String thumbnail, String name, RestaurantType restaurantType, Address address) {
        this.restaurantId = restaurantId;
        this.thumbnail = thumbnail;
        this.name = name;
        this.restaurantType = restaurantType;
        this.address = address;
    }
}

package com.beginvegan.domain.restaurant.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class RestaurantAndMenusRes {

    private RestaurantDetailRes restaurant;
    private List<MenuDetailRes> menus;

    @Builder
    public RestaurantAndMenusRes(RestaurantDetailRes restaurant, List<MenuDetailRes> menus) {
        this.restaurant = restaurant;
        this.menus = menus;
    }

}

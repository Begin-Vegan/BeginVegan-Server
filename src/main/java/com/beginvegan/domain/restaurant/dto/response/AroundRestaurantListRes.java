package com.beginvegan.domain.restaurant.dto.response;

import com.beginvegan.domain.restaurant.domain.Address;
import com.beginvegan.domain.restaurant.dto.MenuDto;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AroundRestaurantListRes {

    private Long id;

    private String name;

    private String businessHours;

    private Address address;

    private String latitude;

    private String longitude;

    private String imageUrl;

    private List<MenuDto> menus = new ArrayList<>();

    @Builder
    public AroundRestaurantListRes(Long id, String name, String businessHours, Address address, String latitude, String longitude, String imageUrl, List<MenuDto> menus) {
        this.id = id;
        this.name = name;
        this.businessHours = businessHours;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.menus = menus;
    }
}

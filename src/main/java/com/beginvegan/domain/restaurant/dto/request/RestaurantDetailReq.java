package com.beginvegan.domain.restaurant.dto.request;

import lombok.Data;

@Data
public class RestaurantDetailReq {

    private Long restaurantId;

    private String filter;

}

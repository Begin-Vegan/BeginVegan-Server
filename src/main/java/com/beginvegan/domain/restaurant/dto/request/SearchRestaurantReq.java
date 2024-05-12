package com.beginvegan.domain.restaurant.dto.request;

import lombok.Data;

@Data
public class SearchRestaurantReq {

    private String latitude;

    private String longitude;

    private String searchWord;

    private String filter; // 리뷰, 스크랩, 거리

}

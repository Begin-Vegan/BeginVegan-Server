package com.beginvegan.domain.restaurant.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RestaurantType {
    CAFE("카페"),
    //RESTAURANT("레스토랑"), // 필요 없어 보임
    WESTERN("양식"),
    CHINESE("중식"), 
    BAKERY("베이커리"),
    KOR("한식"),
    JAPANESE("일식"),
    ETC("기타");

    private String value;
}

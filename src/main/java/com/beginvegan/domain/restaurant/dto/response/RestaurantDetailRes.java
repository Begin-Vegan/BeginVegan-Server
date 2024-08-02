package com.beginvegan.domain.restaurant.dto.response;

import com.beginvegan.domain.restaurant.domain.Address;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.restaurant.domain.RestaurantType;
import lombok.Builder;
import lombok.Data;

@Data
public class RestaurantDetailRes {

    private Long restaurantId;

    private String thumbnail;

    private String name;

    private RestaurantType restaurantType;

    private Address address;

    private Double distance; // 내 위치와의 거리

    private Double rate;

    // 리뷰 수 필요
    private int reviewCount;

    private boolean isBookmark; // 북마크 여부

    private String contactNumber; // 전화번호

    @Builder
    public RestaurantDetailRes(Long restaurantId, String thumbnail, String name, RestaurantType restaurantType, Address address, Double distance, Double rate, int reviewCount, boolean isBookmark, String contactNumber) {
        this.restaurantId = restaurantId;
        this.thumbnail = thumbnail;
        this.name = name;
        this.restaurantType = restaurantType;
        this.address = address;
        this.distance = distance;
        this.rate = rate;
        this.reviewCount = reviewCount;
        this.isBookmark = isBookmark;
        this.contactNumber = contactNumber;
    }

    public static RestaurantDetailRes toDto(Restaurant restaurant) {
        return RestaurantDetailRes.builder()
                .restaurantId(restaurant.getId())
                .name(restaurant.getName())
                .contactNumber(restaurant.getContactNumber())
                .address(restaurant.getAddress())
                .restaurantType(restaurant.getRestaurantType())
                .build();
    }
}

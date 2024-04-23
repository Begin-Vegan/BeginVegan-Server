package com.beginvegan.domain.bookmark.dto;

import com.beginvegan.domain.restaurant.dto.response.RestaurantDetailRes;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class BookmarkListRes {

    private List<RestaurantDetailRes> restaurants;
    private Long totalCount;

    @Builder
    public BookmarkListRes(List<RestaurantDetailRes> restaurants, Long totalCount) {
        this.restaurants = restaurants;
        this.totalCount = totalCount;
    }

}


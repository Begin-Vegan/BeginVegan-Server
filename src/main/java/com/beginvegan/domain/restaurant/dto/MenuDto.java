package com.beginvegan.domain.restaurant.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class MenuDto {

    private Long id;

    private String imageUrl;

    @Builder
    public MenuDto(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }
}
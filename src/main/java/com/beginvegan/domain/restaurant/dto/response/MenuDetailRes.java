package com.beginvegan.domain.restaurant.dto.response;

import com.beginvegan.domain.restaurant.domain.Menu;
import lombok.Builder;
import lombok.Data;

@Data
public class MenuDetailRes {

    private Long id;
    private String name;
    private String price;
    private String description;
    private String imageUrl;

    @Builder
    public MenuDetailRes(Long id, String name, String price, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static MenuDetailRes toDto(Menu menu) {
        return MenuDetailRes.builder()
                .id(menu.getId())
                .name(menu.getName())
//                .price(menu.getPrice())
//                .description(menu.getDescription())
//                .imageUrl(menu.getImageUrl())
                .build();
    }

}

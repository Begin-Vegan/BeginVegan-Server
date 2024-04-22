package com.beginvegan.domain.restaurant.dto.response;

import com.beginvegan.domain.restaurant.domain.Menu;
import lombok.Builder;
import lombok.Data;

@Data
public class MenuDetailRes {

    private Long id;
    private String name;

    @Builder
    public MenuDetailRes(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static MenuDetailRes toDto(Menu menu) {
        return MenuDetailRes.builder()
                .id(menu.getId())
                .name(menu.getName())
                .build();
    }

}

package com.beginvegan.domain.food.dto.response;
import com.beginvegan.domain.user.domain.VeganType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FoodListRes {
    private Long id;

    private String name;

    private VeganType veganType;

    private Boolean isBookmarked;

    @Builder
    public FoodListRes(Long id, String name, VeganType veganType, Boolean isBookmarked) {
        this.id = id;
        this.name = name;
        this.veganType = veganType;
        this.isBookmarked = isBookmarked;
    }
}
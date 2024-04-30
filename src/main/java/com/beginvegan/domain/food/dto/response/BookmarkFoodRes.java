package com.beginvegan.domain.food.dto.response;

import com.beginvegan.domain.user.domain.VeganType;
import lombok.Builder;
import lombok.Data;

@Data
public class BookmarkFoodRes {

    // id, 레시피 이름, 채식 성향

    private Long foodId;

    private String name;

    private VeganType veganType;

    @Builder
    public BookmarkFoodRes(Long foodId, String name, VeganType veganType) {
        this.foodId = foodId;
        this.name = name;
        this.veganType = veganType;
    }
}

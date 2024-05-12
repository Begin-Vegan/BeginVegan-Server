package com.beginvegan.domain.food.dto.response;
import com.beginvegan.domain.user.domain.VeganType;
import lombok.Builder;
import lombok.Data;
@Data
public class FoodListRes {
    private Long id;
    private String name;

    private VeganType veganType;

    @Builder
    public FoodListRes(Long id, String name, VeganType veganType) {
        this.id = id;
        this.name = name;
        this.veganType = veganType;
    }
}
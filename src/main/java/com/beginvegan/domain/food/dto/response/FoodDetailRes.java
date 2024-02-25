package com.beginvegan.domain.food.dto.response;

import com.beginvegan.domain.block.dto.BlockDto;
import com.beginvegan.domain.food.dto.FoodIngredientDto;
import com.beginvegan.domain.user.domain.VeganType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class FoodDetailRes {

    private Long id;

    private String name;

    private VeganType veganType;

    private String source;

    private List<FoodIngredientDto> ingredients;

    private List<BlockDto> blocks;

    @Builder
    public FoodDetailRes(Long id, String name, VeganType veganType, String source, List<FoodIngredientDto> ingredients, List<BlockDto> blocks) {
        this.id = id;
        this.name = name;
        this.veganType = veganType;
        this.ingredients = ingredients;
        this.source = source;
        this.blocks = blocks;
    }
}

package com.beginvegan.domain.food.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class FoodIngredientDto {

    private Long id;

    private String name;

    @Builder
    public FoodIngredientDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
  
}

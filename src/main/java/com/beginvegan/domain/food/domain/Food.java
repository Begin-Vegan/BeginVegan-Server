package com.beginvegan.domain.food.domain;

import com.beginvegan.domain.common.BaseEntity;
import com.beginvegan.domain.block.domain.Block;
import com.beginvegan.domain.user.domain.VeganType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Food extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    private VeganType veganType;

    private String editor;

    @OneToMany(mappedBy = "food")
    private List<FoodIngredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "food")
    private List<Block> foodBlocks = new ArrayList<>();

    @Builder

    public Food(Long id, String name, VeganType veganType, String editor, List<FoodIngredient> ingredients, List<Block> foodBlocks) {
        this.id = id;
        this.name = name;
        this.veganType = veganType;
        this.editor = editor;
        this.ingredients = ingredients;
        this.foodBlocks = foodBlocks;
    }
}

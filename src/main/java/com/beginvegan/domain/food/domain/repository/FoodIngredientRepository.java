package com.beginvegan.domain.food.domain.repository;

import com.beginvegan.domain.food.domain.FoodIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodIngredientRepository extends JpaRepository<FoodIngredient, Long> {
}

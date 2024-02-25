package com.beginvegan.domain.food.domain.repository;

import com.beginvegan.domain.food.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}

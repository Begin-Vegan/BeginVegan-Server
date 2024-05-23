package com.beginvegan.domain.food.domain.repository;

import com.beginvegan.domain.food.domain.Food;
import com.beginvegan.domain.user.domain.VeganType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FoodRepository extends JpaRepository<Food, Long> {
    Page<Food> findAllByVeganType(VeganType veganType, Pageable pageable);

}

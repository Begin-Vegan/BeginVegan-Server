package com.beginvegan.domain.restaurant.domain.repository;

import com.beginvegan.domain.restaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @EntityGraph(attributePaths = {"menus"})
    Optional<Restaurant> findRestaurantById(Long id);

    @EntityGraph(attributePaths = {"menus"})
    @Query("SELECT r FROM Restaurant r")
    List<Restaurant> findAllWithMenus();

}

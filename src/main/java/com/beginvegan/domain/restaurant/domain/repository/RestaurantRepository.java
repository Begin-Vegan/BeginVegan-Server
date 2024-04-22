package com.beginvegan.domain.restaurant.domain.repository;

import com.beginvegan.domain.restaurant.domain.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @EntityGraph(attributePaths = {"menus"})
    Optional<Restaurant> findRestaurantById(Long id);

    @EntityGraph(attributePaths = {"menus"})
    @Query("SELECT r FROM Restaurant r")
    List<Restaurant> findAllWithMenus();

    @Query(value = "SELECT *, (" +
            "6371 * acos(" +
            "cos(radians(:userLatitude)) * cos(radians(latitude)) *" +
            "cos(radians(longitude) - radians(:userLongitude)) +" +
            "sin(radians(:userLatitude)) * sin(radians(latitude))" +
            ")" +
            ") AS distance " +
            "FROM Restaurant " +
            "ORDER BY distance ASC",
            countQuery = "SELECT count(*) FROM Restaurant",
            nativeQuery = true)
    Page<Restaurant> findRestaurantsNearUser(@Param("userLatitude") double userLatitude,
                                             @Param("userLongitude") double userLongitude,
                                             Pageable pageable);
}

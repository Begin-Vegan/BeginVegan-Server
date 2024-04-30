package com.beginvegan.domain.review.domain.repository;

import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.review.domain.Review;
import com.beginvegan.domain.suggestion.domain.parent.Inspection;
import com.beginvegan.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"restaurant", "user"})
    Page<Review> findReviewsByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Review> findReviewsByRestaurant(Restaurant restaurant, Pageable pageable);

    int countAllByRestaurant(Restaurant restaurant);

    // 추천순 정렬
    @Query("SELECT r FROM Review r LEFT JOIN Recommendation rec ON r.id = rec.review.id GROUP BY r.id ORDER BY COUNT(rec) DESC")
    Page<Review> findReviewsOrderByRecommendationCount(Pageable pageable);

    @Query("SELECT DISTINCT res FROM Review r JOIN r.restaurant res WHERE r.modifiedDate = :modifiedDate")
    List<Restaurant> findDistinctRestaurantsByModifiedDate(LocalDate modifiedDate);

    @Query("SELECT AVG(r.rate) FROM Review r WHERE r.restaurant = :restaurant AND r.visible = true")
    BigDecimal findAverageRateByRestaurant(Restaurant restaurant);

}

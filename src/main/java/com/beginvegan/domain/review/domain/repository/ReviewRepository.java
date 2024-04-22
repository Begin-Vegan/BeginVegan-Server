package com.beginvegan.domain.review.domain.repository;

import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.review.domain.Review;
import com.beginvegan.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"restaurant", "user"})
    Page<Review> findReviewsByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Review> findReviewsByRestaurant(Restaurant restaurant, Pageable pageable);

    int countAllByRestaurant(Restaurant restaurant);

}

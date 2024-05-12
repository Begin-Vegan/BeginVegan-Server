package com.beginvegan.domain.recommendation.domain.repository;

import com.beginvegan.domain.common.Status;
import com.beginvegan.domain.recommendation.domain.Recommendation;
import com.beginvegan.domain.review.domain.Review;
import com.beginvegan.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    boolean existsByUserAndReview(User user, Review review);

    int countByReview(Review review);

    Recommendation findByUserAndReview(User user, Review review);

    int countByReviewAndStatus(Review review, Status status);

    boolean existsByUserAndReviewAndStatus(User user, Review review, Status status);
}

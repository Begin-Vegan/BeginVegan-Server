package com.beginvegan.domain.recommendation.domain.repository;

import com.beginvegan.domain.recommendation.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
}

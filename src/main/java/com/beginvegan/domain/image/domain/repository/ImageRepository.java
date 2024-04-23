package com.beginvegan.domain.image.domain.repository;

import com.beginvegan.domain.image.domain.Image;
import com.beginvegan.domain.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByReview(Review review);
}

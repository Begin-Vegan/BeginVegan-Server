package com.beginvegan.domain.image.domain;

import com.beginvegan.domain.common.BaseEntity;
import com.beginvegan.domain.review.domain.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Image extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    private String imageUrl;

    @Builder
    public Image(Long id, Review review, String imageUrl) {
        this.id = id;
        this.review = review;
        this.imageUrl = imageUrl;
    }
}

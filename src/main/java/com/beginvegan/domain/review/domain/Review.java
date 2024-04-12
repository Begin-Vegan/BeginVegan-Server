package com.beginvegan.domain.review.domain;

import com.beginvegan.domain.common.BaseEntity;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.suggestion.domain.parent.Inspection;
import com.beginvegan.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Review extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String thumbnail;

    private Double rate;

    private Boolean verified = false;

    private Boolean visible = true;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    @Enumerated(EnumType.STRING)
    private Inspection inspection; // 검수 여부

    @Builder
    public Review(Long id, String content, LocalDate date, Restaurant restaurant, User user, String thumbnail, Double rate, Boolean verified, Boolean visible, ReviewType reviewType, Inspection inspection) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.restaurant = restaurant;
        this.user = user;
        this.thumbnail = thumbnail;
        this.rate = rate;
        this.verified = verified;
        this.visible = visible;
        this.reviewType = reviewType;
        this.inspection = inspection;
    }
}

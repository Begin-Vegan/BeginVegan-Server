package com.beginvegan.domain.review.domain;

import com.beginvegan.domain.common.BaseEntity;
import com.beginvegan.domain.restaurant.domain.Restaurant;
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

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    public Review(Long id, String content, LocalDate date, Restaurant restaurant, User user) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.restaurant = restaurant;
        this.user = user;
    }

}

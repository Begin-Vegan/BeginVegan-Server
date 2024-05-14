package com.beginvegan.domain.restaurant.domain;

import com.beginvegan.domain.common.BaseEntity;
import com.beginvegan.domain.review.domain.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Restaurant extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String contactNumber;

    @Enumerated(EnumType.STRING)
    private RestaurantType restaurantType;

    @Embedded
    private Address address;

    private String latitude;

    private String longitude;

    private String kakaoMapUrl;

    private String thumbnail;

    private String thumbnail_source;

    private Double rate;

    @OneToMany(mappedBy = "restaurant")
    List<Menu> menus = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant")
    List<Review> reviews = new ArrayList<>();

    @Builder
    public Restaurant(Long id, String name, String contactNumber, RestaurantType restaurantType, Address address, String latitude, String longitude, String kakaoMapUrl, String thumbnail, String thumbnail_source, Double rate, List<Menu> menus, List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.contactNumber = contactNumber;
        this.restaurantType = restaurantType;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kakaoMapUrl = kakaoMapUrl;
        this.thumbnail = thumbnail;
        this.thumbnail_source = thumbnail_source;
        this.rate = rate;
        this.menus = menus;
        this.reviews = reviews;
    }

    public void updateRate(Double rate) { this.rate = rate; }
}

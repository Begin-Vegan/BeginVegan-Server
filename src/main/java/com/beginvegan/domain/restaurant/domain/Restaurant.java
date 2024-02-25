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

    private String businessHours;

    private String contactNumber;

    @Enumerated(EnumType.STRING)
    private RestaurantType restaurantType;

    @Embedded
    private Address address;

    private String latitude;

    private String longitude;

    private String kakaoMapUrl;

    private String imageUrl;

    private String imageSource;

    @OneToMany(mappedBy = "restaurant")
    List<Menu> menus = new ArrayList<>();

    @Builder
    public Restaurant(Long id, String name, String businessHours, String contactNumber, RestaurantType restaurantType, Address address, String latitude, String longitude, String kakaoMapUrl, String imageUrl, String imageSource) {
        this.id = id;
        this.name = name;
        this.businessHours = businessHours;
        this.contactNumber = contactNumber;
        this.restaurantType = restaurantType;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kakaoMapUrl = kakaoMapUrl;
        this.imageUrl = imageUrl;
        this.imageSource = imageSource;
    }

}

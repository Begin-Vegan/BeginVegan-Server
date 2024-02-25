package com.beginvegan.domain.restaurant.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Embeddable
@Getter
public class Address {

    private String province;

    private String city;

    private String roadName;

    private String detailAddress;

    public Address(String province, String city, String roadName, String detailAddress) {
        this.province = province;
        this.city = city;
        this.roadName = roadName;
        this.detailAddress = detailAddress;
    }

}

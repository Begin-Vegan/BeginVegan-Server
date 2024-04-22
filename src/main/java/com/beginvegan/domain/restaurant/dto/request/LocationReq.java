package com.beginvegan.domain.restaurant.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LocationReq {

    private String latitude;

    private String longitude;

}

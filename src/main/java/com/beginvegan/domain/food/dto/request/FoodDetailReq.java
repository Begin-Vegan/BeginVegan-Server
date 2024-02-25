package com.beginvegan.domain.food.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class FoodDetailReq {

    private Long id;

    public FoodDetailReq(Long id) {
        this.id = id;
    }
}

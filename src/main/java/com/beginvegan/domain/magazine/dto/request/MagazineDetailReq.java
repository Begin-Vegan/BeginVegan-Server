package com.beginvegan.domain.magazine.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class MagazineDetailReq {

    private Long id;

    public MagazineDetailReq(Long id) {
        this.id = id;
    }
}

package com.beginvegan.domain.suggestion.dto.request;

import lombok.Data;

@Data
public class RegistReq {

    // 식당 이름, 식당 위치, 식당 설명 / 제보 유형(Suggestion Type), 검수 여부 (Inspection - 처음엔 모두 INCOMPLETE)

    private String name;

    private String location;

    private String content;

}

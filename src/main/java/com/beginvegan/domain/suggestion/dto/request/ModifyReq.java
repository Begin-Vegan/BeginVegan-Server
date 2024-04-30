package com.beginvegan.domain.suggestion.dto.request;

import com.beginvegan.domain.suggestion.domain.parent.SuggestionType;
import lombok.Data;

@Data
public class ModifyReq {

    // 식당 id, 제보 내용, 제보 유형(Suggestion Type), 검수 여부 (Inspection - 처음엔 모두 INCOMPLETE)

    private Long restaurantId;

    private String content;
}

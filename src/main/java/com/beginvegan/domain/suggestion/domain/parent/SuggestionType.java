package com.beginvegan.domain.suggestion.domain.parent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SuggestionType {

    MODIFICATION("MODIFICATION"), // 식당 정보 수정 제안
    REGISTRATION("REGISTRATION"); // 신규 식당 등록 제안

    private String value;
}

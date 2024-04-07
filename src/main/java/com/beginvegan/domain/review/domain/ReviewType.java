package com.beginvegan.domain.review.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReviewType {

    NORMAL("NORMAL"),
    PHOTO("PHOTO"),
    REPORT("REPORT");

    private String value;
}

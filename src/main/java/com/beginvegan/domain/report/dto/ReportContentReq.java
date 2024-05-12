package com.beginvegan.domain.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReportContentReq {

    @NotBlank
    @Size(max = 100, message = "최대 100자까지 입력 가능합니다.")
    private String content;
}

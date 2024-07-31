package com.beginvegan.domain.block.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class BlockDto {

    private String content;

    private Integer sequence;

    private Boolean isBold;

    @Builder
    public BlockDto(Long id, String content, Integer sequence, Boolean isBold) {
        this.content = content;
        this.sequence = sequence;
        this.isBold = isBold;
    }
}

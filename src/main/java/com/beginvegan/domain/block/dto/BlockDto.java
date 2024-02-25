package com.beginvegan.domain.block.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class BlockDto {

    private String content;

    private Integer sequence;

    @Builder
    public BlockDto(Long id, String content, Integer sequence) {
        this.content = content;
        this.sequence = sequence;
    }
}

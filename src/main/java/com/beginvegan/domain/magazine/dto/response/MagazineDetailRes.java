package com.beginvegan.domain.magazine.dto.response;

import com.beginvegan.domain.block.domain.Block;
import com.beginvegan.domain.block.dto.BlockDto;
import com.beginvegan.domain.magazine.domain.MagazineType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MagazineDetailRes {

    private Long id;

    private String title;

    private String editor;

    private String source;

    private String thumbnail;

    private Boolean isBookmarked;

    private LocalDateTime createdDate;

    private List<BlockDto> magazineContents; // magazineBlocks

    @Builder
    public MagazineDetailRes(Long id, String title, String editor, String source, String thumbnail, Boolean isBookmarked, LocalDateTime createdDate, List<BlockDto> magazineContents) {
        this.id = id;
        this.title = title;
        this.editor = editor;
        this.source = source;
        this.thumbnail = thumbnail;
        this.isBookmarked = isBookmarked;
        this.createdDate = createdDate;
        this.magazineContents = magazineContents;
    }
}

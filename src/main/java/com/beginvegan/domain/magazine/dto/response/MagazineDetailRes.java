package com.beginvegan.domain.magazine.dto.response;

import com.beginvegan.domain.block.domain.Block;
import com.beginvegan.domain.block.dto.BlockDto;
import com.beginvegan.domain.magazine.domain.MagazineType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class MagazineDetailRes {

    private Long id;

    private String title;

    private String editor;

    private String source;

    private MagazineType magazineType;

    private List<BlockDto> magazineContents; // magazineBlocks

    @Builder
    public MagazineDetailRes(Long id, String title, String editor, String source, MagazineType magazineType, List<BlockDto> magazineContents) {
        this.id = id;
        this.title = title;
        this.editor = editor;
        this.source = source;
        this.magazineType = magazineType;
        this.magazineContents = magazineContents;
    }
}

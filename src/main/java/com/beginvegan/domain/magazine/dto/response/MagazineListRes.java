package com.beginvegan.domain.magazine.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MagazineListRes {

    private Long id;

    private String title;

    private String editor;

    private LocalDateTime createdDate;


    @Builder
    public MagazineListRes(Long id, String title, String editor, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.editor = editor;
        this.createdDate = createdDate;
    }
}

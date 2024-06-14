package com.beginvegan.domain.magazine.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MagazineListRes {

    private Long id;
    private String title;
    private String thumbnail;
    private String editor;
    private LocalDateTime createdDate;
    private Boolean isBookmarked;


    @Builder
    public MagazineListRes(Long id, String title, String thumbnail, String editor, LocalDateTime createdDate, Boolean isBookmarked) {
        this.id = id;
        this.title = title;
        this.thumbnail = thumbnail;
        this.editor = editor;
        this.createdDate = createdDate;
        this.isBookmarked = isBookmarked;
    }
}

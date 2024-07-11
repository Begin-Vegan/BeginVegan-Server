package com.beginvegan.domain.magazine.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookmarkMagazineRes {

    // 썸네일, 제목, 작성 일시, 작성자

    private Long magazineId;

    private String thumbnail;

    private String title;

    private LocalDate writeTime;

    private String editor;

    @Builder
    public BookmarkMagazineRes(Long magazineId, String thumbnail, String title, LocalDate writeTime, String editor) {
        this.magazineId = magazineId;
        this.thumbnail = thumbnail;
        this.title = title;
        this.writeTime = writeTime;
        this.editor = editor;
    }
}

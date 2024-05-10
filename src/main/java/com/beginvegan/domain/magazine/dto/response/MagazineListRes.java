package com.beginvegan.domain.magazine.dto.response;

import com.beginvegan.domain.magazine.domain.MagazineType;
import lombok.Builder;
import lombok.Data;

@Data
public class MagazineListRes {

    private Long id;

    private String title;

    private String editor;


    @Builder
    public MagazineListRes(Long id, String title, String editor) {
        this.id = id;
        this.title = title;
        this.editor = editor;
    }
}

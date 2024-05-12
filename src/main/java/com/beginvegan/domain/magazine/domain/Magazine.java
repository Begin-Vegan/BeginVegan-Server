package com.beginvegan.domain.magazine.domain;

import com.beginvegan.domain.block.domain.Block;
import com.beginvegan.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Magazine extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String editor;

    private String thumbnail;


    @OneToMany(mappedBy = "magazine")
    private List<Block> magazineBlocks = new ArrayList<>();

    @Builder

    public Magazine(Long id, String title, String editor, String thumbnail, List<Block> magazineBlocks) {
        this.id = id;
        this.title = title;
        this.editor = editor;
        this.thumbnail = thumbnail;
        this.magazineBlocks = magazineBlocks;
    }
}

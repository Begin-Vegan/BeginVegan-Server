package com.beginvegan.domain.block.domain;

import com.beginvegan.domain.common.BaseEntity;
import com.beginvegan.domain.food.domain.Food;
import com.beginvegan.domain.magazine.domain.Magazine;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Block extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Integer sequence;

    private Boolean isBold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "magazine_id")
    private Magazine magazine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    @Builder
    public Block(Long id, String content, Integer sequence, Boolean isBold, Magazine magazine, Food food) {
        this.id = id;
        this.content = content;
        this.sequence = sequence;
        this.isBold = isBold;
        this.magazine = magazine;
        this.food = food;
    }

}

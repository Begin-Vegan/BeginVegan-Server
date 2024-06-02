package com.beginvegan.domain.user.domain;


import lombok.Getter;

@Getter
public enum UserLevel {

    SEED(0),
    ROOT(1),
    SPROUT(2),
    STEM(3),
    LEAF(4),
    TREE(5),
    FLOWER(6) ,
    FRUIT(7);

    private final int order;

    UserLevel(int order) {
        this.order = order;
    }

}

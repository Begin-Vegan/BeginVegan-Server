package com.beginvegan.domain.user.domain;


public enum VeganType {
    UNKNOWN(0),
    VEGAN(1),
    LACTO_VEGETARIAN(2),
    OVO_VEGETARIAN(3),
    LACTO_OVO_VEGETARIAN(4),
    PASCATARIAN(5),
    POLLOTARIAN(6),
    FLEXITARIAN(7);

    private final int order;

    VeganType(int order) {
        this.order = order;
    }
    public int getOrder() {
        return order;
    }
}

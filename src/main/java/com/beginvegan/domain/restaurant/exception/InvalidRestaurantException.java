package com.beginvegan.domain.restaurant.exception;

public class InvalidRestaurantException extends RuntimeException{

    public InvalidRestaurantException() {
        super("유효하지 않는 Restaurant 정보입니다.");
    }

}

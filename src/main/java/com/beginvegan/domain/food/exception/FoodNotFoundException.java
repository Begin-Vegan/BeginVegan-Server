package com.beginvegan.domain.food.exception;

public class FoodNotFoundException extends RuntimeException {

    public FoodNotFoundException(String message) {
        super(message);
    }

    public FoodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

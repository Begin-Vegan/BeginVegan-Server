package com.beginvegan.domain.magazine.exception;

public class MagazineNotFoundException extends RuntimeException{
    public MagazineNotFoundException(String message) {
        super(message);
    }

    public MagazineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}


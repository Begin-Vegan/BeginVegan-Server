package com.beginvegan.domain.user.exception;

public class InvalidUserException extends RuntimeException{

    public InvalidUserException() {
        super("유효하지 않는 유저 정보입니다.");
    }

}

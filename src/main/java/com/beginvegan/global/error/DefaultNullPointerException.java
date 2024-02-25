package com.beginvegan.global.error;

import com.beginvegan.global.payload.ErrorCode;

import lombok.Getter;

@Getter
public class DefaultNullPointerException extends NullPointerException{
    
    private ErrorCode errorCode;

    public DefaultNullPointerException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}

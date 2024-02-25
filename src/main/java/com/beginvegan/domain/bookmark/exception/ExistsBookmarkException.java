package com.beginvegan.domain.bookmark.exception;

public class ExistsBookmarkException extends RuntimeException{

    public ExistsBookmarkException() {
        super("이미 스크랩이 되어 있습니다.");
    }

}

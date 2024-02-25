package com.beginvegan.domain.bookmark.exception;

public class NotExistsBookmarkException extends RuntimeException{

    public NotExistsBookmarkException() {
        super("스크랩 되어있지 않습니다.");
    }
}

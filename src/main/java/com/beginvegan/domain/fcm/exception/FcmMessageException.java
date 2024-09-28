package com.beginvegan.domain.fcm.exception;

import com.google.firebase.messaging.MessagingErrorCode;
import lombok.Getter;

@Getter
public class FcmMessageException extends RuntimeException {

    private MessagingErrorCode messagingErrorCode;

    public FcmMessageException(MessagingErrorCode messagingErrorCode, String message) {
        super(message);
        this.messagingErrorCode = messagingErrorCode;
    }
}

package com.beginvegan.domain.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * FCM 전송 Format DTO
 *
 * @author : lee
 * @fileName : FcmMessageDto
 * @since : 2/21/24
 */
@Getter
@AllArgsConstructor
@Builder
public class FcmMessageDto {
    private boolean validateOnly;
    private FcmMessageDto.Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private String token;
        private FcmMessageDto.Notification notification;
        private Map<String, String> data; // 데이터 메시지를 위한 data 필드 추가
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;
        // type?
    }
}
package com.beginvegan.domain.fcm.dto;

import com.beginvegan.domain.alarm.domain.AlarmType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 모바일에서 전달받은 객체
 *
 * @author : lee
 * @fileName : FcmSendDto
 * @since : 2/21/24
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmSendDto {

    private String token;
    private String title;
    private String body;
    private AlarmType alarmType; // 필요시 전달
    private Long itemId; // 필요시 전달, ex. magazine id

    @Builder
    public FcmSendDto(String token, String title, String body, AlarmType alarmType, Long itemId) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.alarmType = alarmType;
        this.itemId = itemId;
    }
}
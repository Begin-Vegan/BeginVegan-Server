package com.beginvegan.domain.alarm.dto;

import com.beginvegan.domain.alarm.domain.AlarmType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReadAlarmRes {

    @Schema(type = "Long", example = "1", description = "알림 id입니다.")
    public Long alarmId;

    public AlarmType alarmType;

    public String content;

    public Long itemId;

    public LocalDateTime createdDate;

    public Boolean isRead;
}

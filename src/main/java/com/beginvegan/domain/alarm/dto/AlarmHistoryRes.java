package com.beginvegan.domain.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AlarmHistoryRes {

    public List<UnreadAlarmRes> unreadAlarmResList;

    public List<ReadAlarmRes> readAlarmResList;
}

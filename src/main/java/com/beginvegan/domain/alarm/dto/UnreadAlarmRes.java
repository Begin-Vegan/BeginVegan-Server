package com.beginvegan.domain.alarm.dto;

import com.beginvegan.domain.alarm.domain.AlarmType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UnreadAlarmRes {

    @Schema(type = "Long", example = "1", description = "알림 id입니다.")
    public Long alarmId;

    @Schema(type = "String", example = "MAP, TIPS, MYPAGE, INFORMATION", description = "알림의 종류입니다.")
    public AlarmType alarmType;

    @Schema(type = "String", example = "나만의 식물이 성장했어요. mypage에서 확인해 보세요!", description = "알림의 내용입니다.")
    public String content;

    @Schema(type = "Long", example = "1", description = "alarmType이 MAP인 경우에만 restaurantId를 전달합니다.")
    public Long restaurantId;

    @Schema(type = "Long", example = "1", description = "alarmType에 따른 itemId입니다. TIPS: 매거진 또는 레시피의 id, MAP: 리뷰 id")
    public Long itemId;

    @Schema(type = "LocalDateTime", example = "2024-06-02 06:28:45.966434", description = "알림의 생성 일시입니다.")
    public LocalDateTime createdDate;

    @Schema(type = "Boolean", example = "true", description = "알림의 읽음 여부입니다.")
    public Boolean isRead;
}

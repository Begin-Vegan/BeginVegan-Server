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

    @Schema(type = "String", example = "BJsVBMM0tWkqZY656FX3kDqhkCXfgKBhcPPSPDgaoa0tYTVKYh5Dt7...", description = "유저의 FCM 토큰입니다.")
    private String token;

    @Schema(type = "String", example = "비긴, 비건", description = "알림의 제목입니다.")
    private String title;

    @Schema(type = "String", example = "나만의 식물이 성장했어요. mypage에서 확인해 보세요!", description = "알림의 내용입니다.")
    private String body;

    @Schema(type = "String", example = "MAP, TIPS, MYPAGE, INFORMATION", description = "알림의 종류입니다.")
    public AlarmType alarmType;

    @Schema(type = "Long", example = "1", description = "alarmType에 따른 itemId입니다. MAP: 매거진 또는 레시피의 id, MYPAGE: 리뷰 id")
    public Long itemId;

    @Builder
    public FcmSendDto(String token, String title, String body, AlarmType alarmType, Long itemId) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.alarmType = alarmType;
        this.itemId = itemId;
    }
}
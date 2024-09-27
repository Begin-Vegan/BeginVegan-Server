package com.beginvegan.domain.fcm.dto;

import com.beginvegan.domain.alarm.domain.AlarmType;
import com.beginvegan.domain.fcm.domain.MessageType;
import com.beginvegan.domain.user.domain.UserLevel;
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

    @Schema(type = "String", example = "dNFDkmL3R04:APA91bH5XrLbje0cbjkPv8jSDFg9v4hdOcf6rLtWfFzCqH8zqG7NhrS8Dp", description = "유저의 fcmToken입니다.")
    private String token;

    @Schema(type = "String", example = "비긴, 비건", description = "알림의 제목입니다.")
    private String title;

    @Schema(type = "String", example = "나만의 식물이 성장했어요. mypage에서 확인해 보세요!", description = "알림의 내용입니다.")
    private String body;

    @Schema(type = "String", example = "MAP", description = "알림의 종류입니다. MAP, TIPS, MYPAGE, INFORMATION")
    private AlarmType alarmType;

    @Schema(type = "Long", example = "1", description = "alarmType에 따른 itemId입니다. MAP: 매거진 또는 레시피의 id, MYPAGE: 리뷰 id")
    private Long itemId;

    @Schema(type = "String", example = "LEVEL_UP", description = "LEVEL_UP, REVIEW_RECOMMEND, REVIEW_REPORT")
    private MessageType messageType;

    @Schema(type = "string", example = "SEED", description = "유저의 사용자 레벨로, SEED, ROOT, SPROUT, STEM, LEAF, TREE, FLOWER, FRUIT")
    private UserLevel userLevel;

    @Builder
    public FcmSendDto(String token, String title, String body, AlarmType alarmType, Long itemId, MessageType messageType, UserLevel userLevel) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.alarmType = alarmType;
        this.itemId = itemId;
        this.messageType = messageType;
        this.userLevel = userLevel;
    }


}
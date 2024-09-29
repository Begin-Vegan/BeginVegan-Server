package com.beginvegan.domain.fcm.application;

import com.beginvegan.domain.alarm.domain.Alarm;
import com.beginvegan.domain.alarm.domain.AlarmType;
import com.beginvegan.domain.alarm.domain.repository.AlarmRepository;
import com.beginvegan.domain.fcm.domain.MessageType;
import com.beginvegan.domain.fcm.dto.FcmSendDto;
import com.beginvegan.domain.fcm.exception.FcmMessageException;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.UserLevel;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.global.DefaultAssert;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;


    @Transactional
    public String sendMessageTo(FcmSendDto fcmSendDto) throws FirebaseMessagingException {
        String fcmToken = fcmSendDto.getToken();
        User user = validateUserByToken(fcmToken);

        if (fcmToken == null) {
            throw new IllegalArgumentException("FCM 토큰이 존재하지 않음");
        }

        String res = null;
        if (user.getAlarmSetting()) {
            res = sendCombinedMessage(fcmToken, fcmSendDto);
        } else {
            res = sendDataMessage(fcmToken, fcmSendDto);
        }
        // alarmType이 존재할 경우에만 알림 내역에 저장
        if (fcmSendDto.getAlarmType() != null) {
            saveAlarmHistory(fcmSendDto);
        }
        return res;
    }

    private String sendCombinedMessage(String token, FcmSendDto fcmSendDto) {
        com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(fcmSendDto.getTitle())
                        .setBody(fcmSendDto.getBody())
                        .build())
                .putAllData(createDataMassage(fcmSendDto))  // 데이터 추가
                .build();
        try {
            // FCM 메시지 전송
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            // FirebaseMessagingException 처리
            throw new FcmMessageException(e.getMessagingErrorCode(), e.getMessage());
        }
    }

    private String sendDataMessage(String token, FcmSendDto fcmSendDto) throws FirebaseMessagingException {
        // 데이터 메시지 전송
        com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message.builder()
                .setToken(token)
                .putAllData(createDataMassage(fcmSendDto))
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }

    private Map<String, String> createDataMassage(FcmSendDto fcmSendDto) {
        Map<String, String> data = new HashMap<>();
        data.put("body", fcmSendDto.getBody());
        data.put("itemId", fcmSendDto.getItemId() != null ? fcmSendDto.getItemId().toString() : "");
        data.put("alarmType", fcmSendDto.getAlarmType() != null ? fcmSendDto.getAlarmType().toString() : "");
        data.put("messageType", fcmSendDto.getMessageType() != null ? fcmSendDto.getMessageType().toString() : "");
        if (fcmSendDto.getMessageType() == MessageType.LEVEL_UP) {
            data.put("userLevel", fcmSendDto.getUserLevel().toString());
        }
        return data;
    }

// AccessToken 발급 받기 -> Firebase Admin SDK를 사용하므로 필요하지 않습니다.
// private String getAccessToken() throws IOException {
//     GoogleCredentials googleCredentials = GoogleCredentials
//             .fromStream(new ClassPathResource("firebase/" + firebaseConfigPath).getInputStream())
//             .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//
//     googleCredentials.refreshIfExpired();
//     return googleCredentials.getAccessToken().getTokenValue();
// }

    public FcmSendDto makeFcmSendDto(String token, AlarmType alarmType, Long itemId, String body, MessageType messageType, UserLevel userLevel) {
        return FcmSendDto.builder()
                .token(token)
                .alarmType(alarmType)
                .itemId(itemId)
                .title("비긴, 비건")
                .body(body)
                .messageType(messageType)
                .userLevel(userLevel)
                .build();
    }

    @Transactional
    public void saveAlarmHistory(FcmSendDto fcmSendDto) {
        User user = validateUserByToken(fcmSendDto.getToken());

        Alarm alarm = Alarm.builder()
                .alarmType(fcmSendDto.getAlarmType())
                .itemId(fcmSendDto.getItemId())
                .content(fcmSendDto.getBody())
                .user(user)
                .build();

        alarmRepository.save(alarm);
    }

    private User validateUserByToken(String token) {
        Optional<User> findUser = userRepository.findByFcmToken(token);
        DefaultAssert.isTrue(findUser.isPresent(), "유저 정보가 올바르지 않습니다.");
        return findUser.get();
    }
}
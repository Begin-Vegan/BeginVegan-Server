package com.beginvegan.domain.fcm.application;

import com.beginvegan.domain.alarm.domain.Alarm;
import com.beginvegan.domain.alarm.domain.AlarmType;
import com.beginvegan.domain.alarm.domain.repository.AlarmRepository;
import com.beginvegan.domain.common.Status;
import com.beginvegan.domain.fcm.domain.MessageType;
import com.beginvegan.domain.fcm.dto.FcmMessageDto;
import com.beginvegan.domain.fcm.dto.FcmSendDto;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.UserLevel;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.global.DefaultAssert;
import com.beginvegan.global.payload.ApiResponse;
import com.beginvegan.global.payload.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FcmService {

    @Value("${firebase.key-path}")
    private String firebaseConfigPath;

    @Value("${firebase.project-id}")
    private String projectId;

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";
    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;


    @Transactional
    public ResponseEntity<?> sendMessageTo(FcmSendDto fcmSendDto) throws IOException {
        User user = validateUserById(fcmSendDto.getUserId());
        String msg = "메세지 전송에 실패했습니다(FCM 토큰이 존재하지 않음)";

        String fcmToken = user.getFcmToken();
        if (fcmToken != null) {
            if (user.getAlarmSetting()) {
                sendCombinedMessage(fcmToken, fcmSendDto);
            } else {
                sendDataMessage(fcmToken, fcmSendDto);
            }

            // alarmType이 존재할 경우에만 알림 내역에 저장
            if (fcmSendDto.getAlarmType() != null) {
                saveAlarmHistory(fcmSendDto);
            }
            msg = "알림이 전송되었습니다.";
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(msg)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    private void sendCombinedMessage(String token, FcmSendDto fcmSendDto) throws IOException {
        String message = makeFcmMessage(token, fcmSendDto);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

    private String makeFcmMessage(String token, FcmSendDto fcmSendDto) throws JsonProcessingException {
        FcmMessageDto fcmMessage = FcmMessageDto.builder()
                .validateOnly(false)
                .message(FcmMessageDto.Message.builder()
                        .token(token)
                        .notification(FcmMessageDto.Notification.builder()
                                .title(fcmSendDto.getTitle())
                                .body(fcmSendDto.getBody())
                                .image(null)
                                .build())
                        .data(createDataMassage(fcmSendDto))
                        .build())
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
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

    private void sendDataMessage(String token, FcmSendDto fcmSendDto) throws IOException {
        Map<String, String> data = createDataMassage(fcmSendDto);
        String message = makeDataMessage(token, data);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

    private String makeNotificationMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessageDto fcmMessage = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessageDto.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String makeDataMessage(String targetToken, Map<String, String> data) throws JsonProcessingException {
        FcmMessageDto fcmMessage = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(targetToken)
                        .data(data)  // 데이터 메시지 전송을 위한 data 필드 사용
                        .build())
                .validateOnly(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    // AccessToken 발급 받기. -> Header에 포함하여 푸시 알림 요청
    private String getAccessToken() throws IOException {

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("firebase/" + firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    public FcmSendDto makeFcmSendDto(User user, AlarmType alarmType, Long itemId, String body, MessageType messageType, UserLevel userLevel) {
         return FcmSendDto.builder()
                .userId(user.getId())
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
        User user = validateUserById(fcmSendDto.getUserId());

        Alarm alarm = Alarm.builder()
                .alarmType(fcmSendDto.getAlarmType())
                .itemId(fcmSendDto.getItemId())
                .content(fcmSendDto.getBody())
                .user(user)
                .build();

        alarmRepository.save(alarm);
    }

    private User validateUserById(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        DefaultAssert.isTrue(findUser.isPresent(), "유저 정보가 올바르지 않습니다.");
        return findUser.get();
    }
}
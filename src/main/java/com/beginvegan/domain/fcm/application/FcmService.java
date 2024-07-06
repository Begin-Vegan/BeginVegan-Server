package com.beginvegan.domain.fcm.application;

import com.beginvegan.domain.alarm.domain.Alarm;
import com.beginvegan.domain.alarm.domain.AlarmType;
import com.beginvegan.domain.alarm.domain.repository.AlarmRepository;
import com.beginvegan.domain.common.Status;
import com.beginvegan.domain.fcm.dto.FcmMessageDto;
import com.beginvegan.domain.fcm.dto.FcmSendDto;
import com.beginvegan.domain.user.domain.User;
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
import java.util.List;
import java.util.Optional;

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
        String msg = "메세지 전송에 실패했습니다(FCM 토큰이 존재하지 않음)";

        if (fcmSendDto.getToken() != null) {
            // TODO 알림 설정 여부 확인해서 비허용이면 저장만
            Optional<User> findUser = userRepository.findByFcmToken(fcmSendDto.getToken());
            DefaultAssert.isTrue(findUser.isPresent(), "유저 정보가 올바르지 않습니다.");
            User user = findUser.get();

            msg = "알림이 저장되었습니다.";
            boolean isAlarmSetting = user.getAlarmSetting();
            // 수신 허용이면 푸시알림 전송
            if (isAlarmSetting) {
                String message = makeMessage(fcmSendDto.getToken(), fcmSendDto.getTitle(), fcmSendDto.getBody());

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
                msg = "메세지 전송이 완료되었습니다.";
            }

            // alarmType이 존재할 경우에만 알림 내역에 저장
            if (fcmSendDto.getAlarmType() != null) {
                saveAlarmHistory(fcmSendDto);
            }
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message(msg).build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
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

    // AccessToken 발급 받기. -> Header에 포함하여 푸시 알림 요청
    private String getAccessToken() throws IOException {

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("firebase/" + firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    public FcmSendDto makeFcmSendDto(User user, AlarmType alarmType, Long itemId, String body) {
         return FcmSendDto.builder()
                .token(user.getFcmToken())
                .alarmType(alarmType)
                .itemId(itemId)
                .title("비긴, 비건")   // TODO: title 수정 필요할수도
                .body(body)
                .build();
    }

    @Transactional
    public void saveAlarmHistory(FcmSendDto fcmSendDto) {
        Optional<User> findUser = userRepository.findByFcmToken(fcmSendDto.getToken());
        DefaultAssert.isTrue(findUser.isPresent(), "유저 정보가 올바르지 않습니다.");
        User user = findUser.get();

        Alarm alarm = Alarm.builder()
                .alarmType(fcmSendDto.getAlarmType())
                .itemId(fcmSendDto.getItemId())
                .content(fcmSendDto.getBody())
                .user(user)
                .build();

        alarmRepository.save(alarm);
    }
}
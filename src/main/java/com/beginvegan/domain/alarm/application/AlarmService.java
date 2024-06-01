package com.beginvegan.domain.alarm.application;

import com.beginvegan.domain.alarm.domain.Alarm;
import com.beginvegan.domain.alarm.domain.repository.AlarmRepository;
import com.beginvegan.domain.alarm.dto.AlarmHistoryRes;
import com.beginvegan.domain.alarm.dto.ReadAlarmRes;
import com.beginvegan.domain.alarm.dto.UnreadAlarmRes;
import com.beginvegan.domain.fcm.dto.FcmSendDto;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.global.DefaultAssert;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AlarmService {

    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;
    private final UserService userService;

    // 알림 내역 저장
    @Transactional
    public void savaAlarmHistory(FcmSendDto fcmSendDto) {
        User user = validByToken(fcmSendDto.getToken());
        Alarm alarm = Alarm.builder()
                    .alarmType(fcmSendDto.getAlarmType())
                    .itemId(fcmSendDto.getItemId())
                    .content(fcmSendDto.getBody())
                    .user(user)
                    .build();

        alarmRepository.save(alarm);
    }

    // 확인 상태 변경

    public User validByToken(String token) {
        Optional<User> user = userRepository.findByFcmToken(token);
        DefaultAssert.isTrue(user.isPresent(), "유저 정보가 올바르지 않습니다.");
        return user.get();
    }

    // 알림 허용 여부 확인
    public boolean checkAlarmSetting(User user) {
        return user.getAlarmSetting();
    }
}

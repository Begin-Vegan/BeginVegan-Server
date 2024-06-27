package com.beginvegan.domain.alarm.application;

import com.beginvegan.domain.alarm.domain.Alarm;
import com.beginvegan.domain.alarm.domain.AlarmType;
import com.beginvegan.domain.alarm.domain.repository.AlarmRepository;
import com.beginvegan.domain.alarm.dto.AlarmHistoryRes;
import com.beginvegan.domain.alarm.dto.ReadAlarmRes;
import com.beginvegan.domain.alarm.dto.UnreadAlarmRes;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.review.application.ReviewService;
import com.beginvegan.domain.review.domain.Review;
import com.beginvegan.domain.review.domain.repository.ReviewRepository;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ApiResponse;
import com.beginvegan.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final ReviewService reviewService;
    private final UserService userService;

    // 확인 상태 변경
    // TODO: 미확인 알림 전부 확인 처리
    @Transactional
    public ResponseEntity<?> updateIsRead(UserPrincipal userPrincipal) {
        User user = userService.validateUserById(userPrincipal.getId());
        List<Alarm> unreadAlarms = alarmRepository.findByUserAndIsRead(user, false);

        unreadAlarms.forEach(alarm -> alarm.updateIsRead(true));

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("알림을 모두 확인했습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 알림 내역 조회
    @Transactional
    public ResponseEntity<?> getAlarmHistory(UserPrincipal userPrincipal) {
        User user = userService.validateUserById(userPrincipal.getId());

        List<Alarm> alarms = alarmRepository.findByUser(user);
        // 미확인 알람
        List<UnreadAlarmRes> unreadAlarms = alarms.stream()
                .filter(alarm -> !alarm.getIsRead())
                .map(alarm -> {
                    Long restaurantId = null;
                    if (alarm.getAlarmType() == AlarmType.MAP) {
                        Long reviewId = alarm.getItemId();
                        Review review = reviewService.validateReviewById(reviewId);
                        restaurantId = review.getRestaurant().getId();
                    }
                    return UnreadAlarmRes.builder()
                            .alarmId(alarm.getId())
                            .createdDate(alarm.getCreatedDate())
                            .alarmType(alarm.getAlarmType())
                            .itemId(alarm.getItemId())
                            .restaurantId(restaurantId)
                            .content(alarm.getContent())
                            .isRead(alarm.getIsRead())
                            .build();
                })
                .sorted(Comparator.comparing(UnreadAlarmRes::getCreatedDate))
                .collect(Collectors.toList());

        // 확인 알람
        List<ReadAlarmRes> readAlarms = alarms.stream()
                .filter(alarm -> alarm.getIsRead())
                .map(alarm -> {
                    Long restaurantId = null;
                    if (alarm.getAlarmType() == AlarmType.MAP) {
                        Long reviewId = alarm.getItemId();
                        Review review = reviewService.validateReviewById(reviewId);
                        restaurantId = review.getRestaurant().getId();
                    }
                    return ReadAlarmRes.builder()
                            .alarmId(alarm.getId())
                            .createdDate(alarm.getCreatedDate())
                            .alarmType(alarm.getAlarmType())
                            .restaurantId(restaurantId)
                            .itemId(alarm.getItemId())
                            .content(alarm.getContent())
                            .isRead(alarm.getIsRead())
                            .build();
                })
                .sorted(Comparator.comparing(ReadAlarmRes::getCreatedDate))
                .collect(Collectors.toList());

        // 알림 읽음처리
        updateIsRead(userPrincipal);

        AlarmHistoryRes alarmHistoryRes = AlarmHistoryRes.builder()
                .unreadAlarmResList(unreadAlarms)
                .readAlarmResList(readAlarms)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(alarmHistoryRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 알림 삭제(30일)
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteOldAlarms() {
        LocalDateTime date = LocalDate.now().minusDays(31).atStartOfDay();
        alarmRepository.deleteByCreatedDateBefore(date);
    }
}

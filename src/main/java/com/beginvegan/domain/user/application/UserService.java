package com.beginvegan.domain.user.application;

import com.beginvegan.domain.alarm.domain.AlarmType;
import com.beginvegan.domain.auth.domain.Token;
import com.beginvegan.domain.auth.domain.repository.TokenRepository;
import com.beginvegan.domain.auth.exception.InvalidTokenException;
import com.beginvegan.domain.common.Status;
import com.beginvegan.domain.fcm.application.FcmService;
import com.beginvegan.domain.fcm.dto.FcmSendDto;
import com.beginvegan.domain.s3.application.S3Uploader;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.UserLevel;
import com.beginvegan.domain.user.domain.VeganType;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.domain.user.dto.*;
import com.beginvegan.global.DefaultAssert;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.error.DefaultException;
import com.beginvegan.global.payload.ApiResponse;
import com.beginvegan.global.payload.ErrorCode;
import com.beginvegan.global.payload.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final S3Uploader s3Uploader;
    private final FcmService fcmService;

    private static final int SALT_LENGTH = 16;

    public ResponseEntity<?> findUserByToken(UserPrincipal userPrincipal) {
        User user = validateUserById(userPrincipal.getId());

        UserDetailRes userDetailRes = UserDetailRes.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .provider(user.getProvider())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(userDetailRes);
    }

    @Transactional
    public ResponseEntity<?> updateFcmToken(UserPrincipal userPrincipal, UpdateFcmTokenReq updateFcmTokenReq) {
        User user = validateUserById(userPrincipal.getId());
        user.updateFcmToken(updateFcmTokenReq.getFcmToken());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("FCM 토큰이 업데이트되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // Description : 비건 타입 변경
    @Transactional
    public ResponseEntity<?> updateVeganType(UserPrincipal userPrincipal, UpdateVeganTypeReq updateVeganTypeReq, String type) throws IOException {
        User user = validateUserById(userPrincipal.getId());
        user.updateVeganType(updateVeganTypeReq.getVeganType());
        if (Objects.equals(type, "TEST")) {
            // 최초 1회인지 확인하여 포인트 부여
            rewardInitialVeganTest(user);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("비건 타입이 변경되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> getAlarmSetting(UserPrincipal userPrincipal) {
        User user = validateUserById(userPrincipal.getId());

        AlarmSettingRes alarmSettingRes = AlarmSettingRes.builder()
                .alarmSetting(user.getAlarmSetting()).build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(alarmSettingRes).build();
        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> updateAlarmSetting(UserPrincipal userPrincipal) {
        User user = validateUserById(userPrincipal.getId());
        boolean isAlarmSetting = !user.getAlarmSetting();
        user.updateAlarmSetting(isAlarmSetting);

        UpdateAlarmSettingRes updateAlarmSettingRes = UpdateAlarmSettingRes.builder()
                .alarmSetting(isAlarmSetting).build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(updateAlarmSettingRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> updateProfile(UserPrincipal userPrincipal, UpdateNicknameReq updateNicknameReq, Boolean isDefaultImage, Optional<MultipartFile> file) {
        User user = validateUserById(userPrincipal.getId());

        String newNickname = updateNicknameReq.getNickname();
        if (!Objects.equals(user.getNickname(), newNickname)) {
            // 닉네임 수정
            user.updateUserCode(generateUserCode(newNickname));
            user.updateNickname(newNickname);
        }
        // 이미지 수정
        file.ifPresent(multipartFile -> {
            try { updateProfileImage(user, isDefaultImage, multipartFile);
            } catch (IOException e) { throw new RuntimeException(e); }
        });

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("유저 프로필이 변경되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    private String generateUserCode(String nickname) {
        Optional<User> latestUserOptional = userRepository.findTopByNicknameOrderByUserCodeDesc(nickname);
        int count = latestUserOptional.map(user -> Integer.parseInt(user.getUserCode())).orElse(0);

        return String.format("%04d", count + 1);
    }

    private void updateProfileImage(User user, Boolean isDefaultImage, MultipartFile file) throws IOException {
        if (user.getImageUrl().contains("amazonaws.com/")) {
            // 기존 프로필 이미지 삭제
            String originalFile = user.getImageUrl().split("amazonaws.com/")[1];
            s3Uploader.deleteFile(originalFile);
        }
        String imageUrl = registerImage(isDefaultImage, file);
        user.updateImageUrl(imageUrl);
        // 최초 1회인지 확인하여 포인트 부여
        rewardInitialProfileImage(user, isDefaultImage);
    }

    private String registerImage(Boolean isDefaultImage, MultipartFile file) {
        if (file.isEmpty() && isDefaultImage) {
            return "/profile.png";
        } else if (!file.isEmpty() && !isDefaultImage) {
            return s3Uploader.uploadImage(file);
        } else {
            throw new DefaultException(ErrorCode.INVALID_PARAMETER, "잘못된 요청입니다.");
        }
    }

    // Description : 프로필 최초 설정 시 포인트 지급
    private void rewardInitialProfileImage(User user, Boolean isDefaultImage) throws IOException {
        // 프로필 이미지 설정 여부 확인
        if (!user.getCustomProfileCompleted()) {
            if (!isDefaultImage) {
                user.updatePoint(1);
                user.updateCustomProfileCompleted(true);
            }
        }
        // 사용자 레벨 검증
        checkUserLevel(user);
    }

    // Description : 비건테스트 최초 수행 시 포인트 지급
    private void rewardInitialVeganTest(User user) throws IOException {
        if (!user.getVeganTestCompleted()) {
            user.updatePoint(1);
            user.updateVeganTestCompleted(true);
        }
        // 사용자 레벨 검증
        checkUserLevel(user);
    }

    // 닉네임, 등급별 이미지 출력
    public ResponseEntity<?> getHomeUserInfo(UserPrincipal userPrincipal) {
        User user = validateUserById(userPrincipal.getId());

        String userLevel = user.getUserLevel().toString();
        HomeUserInfoRes homeUserInfoRes = HomeUserInfoRes.builder()
                .nickname(user.getNickname())
                .userLevel(userLevel)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(homeUserInfoRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // userLevel 검증
    // Description: 포인트 변경 시 마다 호출
    private UserLevel countUserLevel(Integer point) {
        UserLevel userLevel;

        if (point < 2) { userLevel = UserLevel.SEED; }
        else if (point < 5) { userLevel = UserLevel.ROOT; }
        else if (point < 10) { userLevel = UserLevel.SPROUT; }
        else if (point < 20) { userLevel = UserLevel.STEM; }
        else if (point < 30) { userLevel = UserLevel.LEAF; }
        else if (point < 50) { userLevel = UserLevel.TREE; }
        else if (point < 100) { userLevel = UserLevel.FLOWER; }
        else { userLevel = UserLevel.FRUIT; }

        return userLevel;
    }

    // userLevel 변경 확인 후 푸시 알림
    @Transactional
    public void checkUserLevel(User user) throws IOException {
        UserLevel originalLevel = user.getUserLevel();
        UserLevel newLevel = countUserLevel(user.getPoint());
        if (originalLevel != newLevel) {
            if (newLevel.getOrder() > originalLevel.getOrder()) {
                String msg = "나만의 식물이 성장했어요. mypage에서 확인해 보세요!";
                FcmSendDto fcmSendDto = fcmService.makeFcmSendDto(user, AlarmType.MYPAGE, null, msg);
                fcmService.sendMessageTo(fcmSendDto);
            }
            user.updateUserLevel(newLevel);
        }
    }

    // Description : 마이페이지 회원 정보 조회
    public ResponseEntity<?> getMyPageUserInfo(UserPrincipal userPrincipal) {
        User user = validateUserById(userPrincipal.getId());

        MyPageUserInfoRes myPageUserInfoRes = MyPageUserInfoRes.builder()
                .id(user.getId())
                .imageUrl(user.getImageUrl())
                .nickname(user.getNickname())
                .userLevel(user.getUserLevel().toString())
                .point(user.getPoint())
                .veganType(user.getVeganType())
                .point(user.getPoint())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(myPageUserInfoRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // Description : 회원 탈퇴
    @Transactional
    public ResponseEntity<?> deleteUser(UserPrincipal userPrincipal) {
        User user = validateUserById(userPrincipal.getId());

        if (user.getImageUrl().contains("amazonaws.com/")) {
            // 기존 프로필 이미지 삭제
            String originalFile = user.getImageUrl().split("amazonaws.com/")[1];
            s3Uploader.deleteFile(originalFile);
        }
        // 유저 정보 변경
        user.updateNickname("알 수 없음");
        user.updateImageUrl("/profile.png");
        user.updateStatus(Status.DELETE);

        // 토큰 삭제
        Token token = tokenRepository.findByUserEmail(user.getEmail())
                .orElseThrow(InvalidTokenException::new);

        tokenRepository.delete(token);

        // 개인정보 해시함수로 암호화
        hashingUser(user);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("탈퇴가 완료되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    private void hashingUser(User user) {
        byte[] salt = generateSalt();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashEmailBytes = digest.digest(concatenate(user.getEmail().getBytes(), salt));
            byte[] hashPasswordBytes = digest.digest(concatenate(user.getPassword().getBytes(), salt));
            byte[] hashProviderIdBytes = digest.digest(concatenate(user.getProviderId().getBytes(), salt));
            byte[] hashFcmTokenBytes = digest.digest(concatenate(user.getFcmToken().getBytes(), salt));

            // 해시된 바이트 배열을 Base64 문자열로 변환
            String hashedEmail = Base64.getEncoder().encodeToString(hashEmailBytes);
            String hashedPassword = Base64.getEncoder().encodeToString(hashPasswordBytes);
            String hashedProviderId = Base64.getEncoder().encodeToString(hashProviderIdBytes);
            String hashedFcmToken = Base64.getEncoder().encodeToString(hashFcmTokenBytes);

            user.softDeleteUser(hashedEmail + "@email.com", hashedPassword, hashedProviderId, hashedFcmToken);
        } catch (NoSuchAlgorithmException e) {
            throw new DefaultException(ErrorCode.INVALID_CHECK, "해시 함수를 찾을 수 없습니다.");
        }
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    // Description : 유효성 검증 함수
    public User validateUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        DefaultAssert.isTrue(user.isPresent(), "유저 정보가 올바르지 않습니다.");
        return user.get();
    }

}

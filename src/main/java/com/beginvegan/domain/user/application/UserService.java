package com.beginvegan.domain.user.application;

import com.beginvegan.domain.s3.application.S3Uploader;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.VeganType;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.domain.user.dto.*;
import com.beginvegan.domain.user.exception.InvalidUserException;
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

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    public ResponseEntity<?> findUserByToken(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.INVALID_CHECK, "유저 정보가 유효하지 않습니다."));

        UserDetailRes userDetailRes = UserDetailRes.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .provider(user.getProvider())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(userDetailRes);
    }

    // Description : 비건테스트 결과 조회
    @Transactional
    public ResponseEntity<?> getVeganTestResult(UserPrincipal userPrincipal, VeganType veganType) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);

        user.updateVeganType(veganType);
        // 최초 1회인지 확인하여 포인트 부여
        rewardInitialVeganTest(user);

        VeganTestResultRes veganTestResultRes = VeganTestResultRes.builder()
                .nickname(user.getNickname())
                .veganType(user.getVeganType())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(veganTestResultRes).build();
        return  ResponseEntity.ok(apiResponse);
    }

    // Description : [마이페이지] 비건 타입 변경
    @Transactional
    public ResponseEntity<?> updateVeganType(UserPrincipal userPrincipal, UpdateVeganTypeReq updateVeganTypeReq) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);

        user.updateVeganType(updateVeganTypeReq.getVeganType());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("비건 타입이 변경되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> getAlarmSetting(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);

        AlarmSettingRes alarmSettingRes = AlarmSettingRes.builder()
                .alarmSetting(user.getAlarmSetting()).build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(alarmSettingRes).build();
        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> updateAlarmSetting(UserPrincipal userPrincipal, UpdateAlarmSettingReq alarmSettingReq) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);

        user.updateAlarmSetting(alarmSettingReq.getAlarmSetting());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("유저의 알림 여부 설정이 완료되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> updateProfile(UserPrincipal userPrincipal, UpdateNicknameReq updateNicknameReq, Boolean isDefaultImage, MultipartFile file) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);

        String newNickname = updateNicknameReq.getNickname();
        if (!Objects.equals(user.getNickname(), newNickname)) {
            // 닉네임 수정
            user.updateUserCode(generateUserCode(newNickname));
            user.updateNickname(newNickname);
        }
        // 이미지 수정
        updateProfileImage(user, isDefaultImage, file);

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

    private void updateProfileImage(User user, Boolean isDefaultImage, MultipartFile file) {
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
    private void rewardInitialProfileImage(User user, Boolean isDefaultImage) {
        // 프로필 이미지 설정 여부 확인
        if (!user.getCustomProfileCompleted()) {
            if (!isDefaultImage) {
                user.updatePoint(1);
                user.updateCustomProfileCompleted(true);
            }
        }
    }

    // Description : 비건테스트 최초 수행 시 포인트 지급
    private void rewardInitialVeganTest(User user) {
        if (!user.getVeganTestCompleted()) {
            user.updatePoint(1);
            user.updateVeganTestCompleted(true);
        }
    }

    // 프로필 수정 시 기존 프로필 이미지 조회
    // public ResponseEntity<?> getMyProfileImage(UserPrincipal userPrincipal) {
    //     User user = userRepository.findById(userPrincipal.getId())
    //             .orElseThrow(InvalidUserException::new);

    //     ApiResponse apiResponse = ApiResponse.builder()
    //             .check(true)
    //             .information(user.getImageUrl())
    //             .build();

    //     return ResponseEntity.ok(apiResponse);
    // }

    // 닉네임, 등급별 이미지 출력
    public ResponseEntity<?> getHomeUserInfo(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.INVALID_CHECK, "유저 정보가 유효하지 않습니다."));

        String userLevel = countUserLevel(user.getPoint());
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

    private String countUserLevel(Integer point) {
        String userLevel;

        if (point < 2) { userLevel = "SEED";}
        else if (point < 5) { userLevel = "ROOT";}
        else if (point < 10) { userLevel = "SPROUT";}
        else if (point < 20) { userLevel = "STEM";}
        else if (point < 30) { userLevel = "LEAF";}
        else if (point < 50) { userLevel = "TREE";}
        else if (point < 100) { userLevel = "FLOWER";}
        else { userLevel = "FRUIT"; }

        return userLevel;
    }

    // Description : 마이페이지 회원 정보 조회
    public ResponseEntity<?> getMyPageUserInfo(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.INVALID_CHECK, "유저 정보가 유효하지 않습니다."));

        MyPageUserInfoRes myPageUserInfoRes = MyPageUserInfoRes.builder()
                .id(user.getId())
                .imageUrl(user.getImageUrl())
                .nickname(user.getNickname())
                .userLevel(countUserLevel(user.getPoint()))
                .veganType(user.getVeganType())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(myPageUserInfoRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

}

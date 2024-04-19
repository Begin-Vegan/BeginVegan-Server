package com.beginvegan.domain.user.application;

import com.beginvegan.domain.s3.application.S3Uploader;
import com.beginvegan.domain.user.domain.User;
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

    // TODO : 비건테스트 최초 1회만 포인트 지급
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
    public ResponseEntity<?> updateNickname(UserPrincipal userPrincipal, UpdateNicknameReq updateNicknameReq) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);

        user.updateUserCode(generateUserCode(updateNicknameReq.getNickname()));
        user.updateNickname(updateNicknameReq.getNickname());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("유저 닉네임이 변경되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    private String generateUserCode(String nickname) {
        Optional<User> latestUserOptional = userRepository.findTopByNicknameOrderByUserCodeDesc(nickname);
        int count = latestUserOptional.map(user -> Integer.parseInt(user.getUserCode())).orElse(0);

        return String.format("%04d", count + 1);
    }

    @Transactional
    public ResponseEntity<?> updateProfileImage(UserPrincipal userPrincipal, Boolean isDefaultImage, MultipartFile file) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);

        // 기존 프로필 이미지 삭제
        String originalFile = user.getImageUrl().split("amazonaws.com/")[1];
        s3Uploader.deleteFile(originalFile);

        String imageUrl = registerImage(isDefaultImage, file);
        // 최초 설정인지 확인하여 포인트 부여
        rewardInitialProfileImage(user, imageUrl);
        user.updateImageUrl(imageUrl);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("유저 프로필이 변경되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
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
    private void rewardInitialProfileImage(User user, String newImageUrl) {
        // 기존 이미지가 aws에 올라가있는지 확인
        if (!user.getImageUrl().contains("amazonaws.com/")) {
            // 이번에 올리는 이미지가 aws 올라갈 경우 포인트 부여
            if (newImageUrl.contains("amazonaws.com/")) {
                user.updatePoint(1);
            }
        }
    }

    // 프로필 수정 시 기존 프로필 조회
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
    public ResponseEntity<?> getUserHomeInfo(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.INVALID_CHECK, "유저 정보가 유효하지 않습니다."));

        String userLevel = countUserLevel(user.getPoint());
        UserHomeInfoRes userHomeInfoRes = UserHomeInfoRes.builder()
                .nickname(user.getNickname())
                .userLevel(userLevel)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(userHomeInfoRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // TODO : 이미지 받으면 s3에 추가하고 이미지 경로로 수정해두기
    private String countUserLevel(Integer point) {
        String userLevel;

        if (point < 2) { userLevel = "seed";}
        else if (point < 5) { userLevel = "root";}
        else if (point < 10) { userLevel = "sprout";}
        else if (point < 20) { userLevel = "stem";}
        else if (point < 30) { userLevel = "leaf";}
        else if (point < 50) { userLevel = "tree";}
        else if (point < 100) { userLevel = "flower";}
        else { userLevel = "fruit"; }

        return userLevel;
    }



}

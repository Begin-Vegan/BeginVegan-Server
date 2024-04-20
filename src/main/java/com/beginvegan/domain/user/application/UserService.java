package com.beginvegan.domain.user.application;

import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.domain.user.dto.UpdateMarketingConsentReq;
import com.beginvegan.domain.user.dto.UpdateNicknameReq;
import com.beginvegan.domain.user.dto.UpdateVeganTypeReq;
import com.beginvegan.domain.user.dto.UserDetailRes;
import com.beginvegan.domain.user.exception.InvalidUserException;
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

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<?> findUserByToken(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.INVALID_CHECK, "유저 정보가 유효하지 않습니다."));

        UserDetailRes userDetailRes = UserDetailRes.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .provider(user.getProvider())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(userDetailRes);
    }

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
    public ResponseEntity<?> updateMarketingConsent(UserPrincipal userPrincipal, UpdateMarketingConsentReq marketingConsentReq) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);

        user.updateMarketingConsent(marketingConsentReq.getMarketingConsent());

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

        user.updateName(updateNicknameReq.getNickname());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("유저 닉네임이 변경되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // Description : 유효성 검증 함수
    public User validateUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        DefaultAssert.isTrue(user.isPresent(), "유저 정보가 올바르지 않습니다.");
        return user.get();
    }

}

package com.beginvegan.domain.auth.application;

import java.util.Optional;
import com.beginvegan.domain.alarm.domain.AlarmType;
import com.beginvegan.domain.auth.dto.*;
import com.beginvegan.domain.auth.exception.InvalidTokenException;
import com.beginvegan.domain.fcm.application.FcmService;
import com.beginvegan.domain.fcm.dto.FcmSendDto;
import com.beginvegan.domain.s3.application.S3Uploader;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.domain.Provider;
import com.beginvegan.domain.user.domain.Role;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.global.DefaultAssert;

import com.beginvegan.domain.auth.domain.Token;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.error.DefaultException;
import com.beginvegan.global.payload.ApiResponse;
import com.beginvegan.global.payload.ErrorCode;
import com.beginvegan.global.payload.Message;
import com.beginvegan.domain.auth.domain.repository.TokenRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final CustomTokenProviderService customTokenProviderService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final FcmService fcmService;


    @Transactional
    public ResponseEntity<?> refresh(RefreshTokenReq tokenRefreshRequest){
        //1차 검증
        boolean checkValid = valid(tokenRefreshRequest.getRefreshToken());
        DefaultAssert.isAuthentication(checkValid);

        Token token = tokenRepository.findByRefreshToken(tokenRefreshRequest.getRefreshToken())
                .orElseThrow(InvalidTokenException::new);
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.getUserEmail());

        //refresh token 정보 값을 업데이트 한다.
        //시간 유효성 확인
        TokenMapping tokenMapping;

        Long expirationTime = customTokenProviderService.getExpiration(tokenRefreshRequest.getRefreshToken());
        if(expirationTime > 0){
            tokenMapping = customTokenProviderService.refreshToken(authentication, token.getRefreshToken());
        }else{
            tokenMapping = customTokenProviderService.createToken(authentication);
        }

        Token updateToken = token.updateRefreshToken(tokenMapping.getRefreshToken());

        AuthRes authResponse = AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(updateToken.getRefreshToken())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> signOut(UserPrincipal userPrincipal){
        Token token = tokenRepository.findByUserEmail(userPrincipal.getEmail())
                .orElseThrow(InvalidTokenException::new);

        tokenRepository.delete(token);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("유저가 로그아웃 되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 추가 정보 입력
    @Transactional
    public ResponseEntity<?> addSignUpUserInfo(UserPrincipal userPrincipal, AddUserInfoReq addUserInfoReq, Boolean isDefaultImage, MultipartFile file) throws FirebaseMessagingException {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.INVALID_CHECK, "유저 정보가 유효하지 않습니다."));

        String userCode = generateUserCode(addUserInfoReq.getNickname());
        String imageUrl = registerImage(isDefaultImage, file);

        user.updateUser(imageUrl, addUserInfoReq.getNickname(), userCode, addUserInfoReq.getVeganType());
        user.updateSignUpCompleted(true);
        rewardInitialProfileImage(user, isDefaultImage);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("추가 정보 등록이 완료되었습니다.").build())
                .build();

        // 웰컴 메세지 전송
        String msg = "비긴, 비건에 오신 것을 환영해요. 비거너의 여정으로 함께 떠나요!";
        FcmSendDto fcmSendDto = fcmService.makeFcmSendDto(user.getFcmToken(), AlarmType.INFORMATION, null, msg, null, null);
        fcmService.sendMessageTo(fcmSendDto);

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

    // UserCode 생성
    private String generateUserCode(String nickname) {
        Optional<User> recentUser = userRepository.findTopByNicknameOrderByUserCodeDesc(nickname);
        int count = recentUser.map(user -> Integer.parseInt(user.getUserCode())).orElse(0);

        return String.format("%04d", count + 1);
    }

    private boolean valid(String refreshToken){

        //1. 토큰 형식 물리적 검증
        boolean validateCheck = customTokenProviderService.validateToken(refreshToken);
        DefaultAssert.isTrue(validateCheck, "Token 검증에 실패하였습니다.");

        //2. refresh token 값을 불러온다.
        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);
        DefaultAssert.isTrue(token.isPresent(), "탈퇴 처리된 회원입니다.");

        //3. email 값을 통해 인증값을 불러온다
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getUserEmail());
        DefaultAssert.isTrue(token.get().getUserEmail().equals(authentication.getName()), "사용자 인증에 실패하였습니다.");

        return true;
    }

    // 회원가입
    public void signUp(String email, String providerId) {
        DefaultAssert.isTrue(!userRepository.existsByEmail(email), "이미 가입된 이메일이 존재합니다.");

        User newUser = User.builder()
                .providerId(providerId)
                .provider(Provider.kakao)
                .email(email)
                .password(passwordEncoder.encode(providerId))
                .role(Role.USER)
                .build();

        userRepository.save(newUser);
    }

    // 비회원: 회원가입 후 로그인
    // 회원: 로그인
    @Transactional
    public ResponseEntity<?> signIn(SignInReq signInReq) {
        Optional<User> userOptional = userRepository.findByEmail(signInReq.getEmail());
        // 비회원
        if (userOptional.isEmpty()) {
            signUp(signInReq.getEmail(), signInReq.getProviderId());
            userOptional = userRepository.findByEmail(signInReq.getEmail());
        }

        //        .orElseThrow(() -> new DefaultException(ErrorCode.INVALID_CHECK, "유저 정보가 유효하지 않습니다."));
        //        DefaultAssert.isTrue(user.getSignUpCompleted(), "회원가입 절차가 완료되지 않았습니다.");

        User user = userOptional.get();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInReq.getEmail(),
                        signInReq.getProviderId()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);
        Token token = Token.builder()
                .refreshToken(tokenMapping.getRefreshToken())
                .userEmail(tokenMapping.getUserEmail())
                .build();
        tokenRepository.save(token);

        AuthRes authResponse = AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();

        SignInRes signInRes = SignInRes.builder()
                .signUpCompleted(user.getSignUpCompleted())
                .authRes(authResponse)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(signInRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // Description : [회원 가입] 프로필 최초 설정 시 포인트 지급
    private void rewardInitialProfileImage(User user, Boolean isDefaultImage) throws FirebaseMessagingException {
        if (!isDefaultImage) {
            user.updatePoint(1);
            userService.checkUserLevel(user);
            // 프로필 이미지 최초 설정하여 값 변경
            user.updateCustomProfileCompleted(true);
        }
    }

}

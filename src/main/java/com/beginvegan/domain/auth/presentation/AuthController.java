package com.beginvegan.domain.auth.presentation;


import jakarta.validation.Valid;

import com.beginvegan.domain.auth.dto.*;
import com.beginvegan.global.payload.ErrorResponse;
import com.beginvegan.global.config.security.token.CurrentUser;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.Message;
import com.beginvegan.domain.auth.application.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Authorization", description = "Authorization API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "유저 회원가입", description = "회원가입을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "회원가입 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping(value="/sign-up")
    public ResponseEntity<?> signUp(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "SignUpReq Schema를 확인해주세요.", required = true) @Valid @RequestBody SignUpReq signUpReq
    ) {
        return authService.signUp(userPrincipal, signUpReq);
    }

    @Operation(summary = "유저 추가 정보 입력", description = "유저 회원가입 이후 추가 정보를 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 입력 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "정보 입력 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping(value="/sign-up/detail")
    public ResponseEntity<?> addUserInfo(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "AddUserInfoReq Schema를 확인해주세요.", required = true) @Valid @RequestPart AddUserInfoReq addUserInfoReq,
            @Parameter(description = "프로필 등록 시 기본 이미지 여부를 입력해주세요.", required = true) @RequestPart Boolean isDefaultImage,
            @Parameter(description = "form-data 형식의 Multipart-file을 입력해주세요.") @RequestPart MultipartFile file
    ) throws IOException {
        return authService.addSignUpUserInfo(userPrincipal, addUserInfoReq, isDefaultImage, file);
    }

    @Operation(summary = "유저 로그인", description = "유저 로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "로그인 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping(value="/sign-in")
    public ResponseEntity<?> signIn(
            @Parameter(description = "SignInReq Schema를 확인해주세요.", required = true) @RequestBody SignInReq signInReq
    ) {
        return authService.signIn(signInReq);
    }

    @Operation(summary = "토큰 갱신", description = "신규 토큰 갱신을 수행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 갱신 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthRes.class) ) } ),
        @ApiResponse(responseCode = "400", description = "토큰 갱신 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refresh(
        @Parameter(description = "Schemas의 RefreshTokenRequest를 참고해주세요.", required = true) @Valid @RequestBody RefreshTokenReq tokenRefreshRequest
    ) {
        return authService.refresh(tokenRefreshRequest);
    }

    @Operation(summary = "유저 로그아웃", description = "유저 로그아웃을 수행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
        @ApiResponse(responseCode = "400", description = "로그아웃 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping(value="/sign-out")
    public ResponseEntity<?> signOut(
        @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return authService.signOut(userPrincipal);
    }

}

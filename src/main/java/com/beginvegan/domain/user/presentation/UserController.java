package com.beginvegan.domain.user.presentation;

import com.beginvegan.domain.auth.dto.AuthRes;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.domain.VeganType;
import com.beginvegan.domain.user.dto.*;
import com.beginvegan.global.config.security.token.CurrentUser;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ErrorResponse;
import com.beginvegan.global.payload.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Users", description = "Users API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "AccessToken을 이용한 유저 정보 조회", description = "AccessToken을 이용한 유저 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "유저 정보 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping
    public ResponseEntity<?> findUserByToken(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return userService.findUserByToken(userPrincipal);
    }

    @Operation(summary = "유저의 알림 여부 조회", description = "유저의 알림 여부를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 알람 여부 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AlarmSettingRes.class))}),
            @ApiResponse(responseCode = "400", description = "유저 알람 여부 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/alarm")
    public ResponseEntity<?> getAlarmSetting(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return userService.getAlarmSetting(userPrincipal);
    }

    @Operation(summary = "유저의 알림 여부 변경", description = "유저의 알림 여부를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 알람 여부 변경 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UpdateAlarmSettingRes.class))}),
            @ApiResponse(responseCode = "400", description = "유저 알람 여부 변경 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/alarm")
    public ResponseEntity<?> updateAlarmSetting(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return userService.updateAlarmSetting(userPrincipal);
    }

    @Operation(summary = "유저 비건 타입 변경", description = "유저의 비건 타입을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 비건 타입 변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "유저 비건 타입 변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PatchMapping("/vegan-type")
    public ResponseEntity<?> updateVeganType(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "UpdateVeganTypeReq Schema를 확인해주세요", required = true) @RequestBody UpdateVeganTypeReq updateVeganTypeReq
            ) {
        return userService.updateVeganType(userPrincipal, updateVeganTypeReq);
    }

    @Operation(summary = "비건 테스트 결과 조회", description = "비건 테스트 결과를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비건 테스트 결과 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VeganTestResultRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "비건 테스트 결과 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/vegan-test/{veganType}")
    public ResponseEntity<?> getVeganTestResult(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "veganType을 입력해주세요.", required = true) @PathVariable VeganType veganType
    ) {
        return userService.getVeganTestResult(userPrincipal, veganType);
    }

    @Operation(summary = "유저 프로필 변경", description = "유저의 프로필을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 프로필 변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "유저 프로필 변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "UpdateNicknameReq Schema를 확인해주세요", required = true) @Valid @RequestPart UpdateNicknameReq updateNicknameReq,
            @Parameter(description = "프로필 변경 시 기본 이미지 여부를 입력해주세요.", required = true) @RequestPart Boolean isDefaultImage,
            @Parameter(description = "form-data 형식의 Multipart-file을 입력해주세요.") @RequestPart MultipartFile file
    ) {
        return userService.updateProfile(userPrincipal, updateNicknameReq, isDefaultImage, file);
    }

    @Operation(summary = "유저 정보 조회(홈)", description = "홈 화면에서 유저의 정보(닉네임, 레벨)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = HomeUserInfoRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "유저 정보 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/home")
    public ResponseEntity<?> getUserHomeInfo(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return userService.getHomeUserInfo(userPrincipal);
    }

    @Operation(summary = "유저 정보 조회(마이페이지)", description = "마이페이지에서 유저의 정보(이미지, 닉네임, 레벨, 비건 타입)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MyPageUserInfoRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "유저 정보 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/my-page")
    public ResponseEntity<?> getMyPageUserInfo(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return userService.getMyPageUserInfo(userPrincipal);
    }

}

package com.beginvegan.domain.user.presentation;

import com.beginvegan.domain.auth.dto.AuthRes;
import com.beginvegan.domain.auth.dto.RefreshTokenReq;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.dto.UpdateMarketingConsentReq;
import com.beginvegan.domain.user.dto.UpdateNicknameReq;
import com.beginvegan.domain.user.dto.UpdateVeganTypeReq;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "유저의 알림 여부 설정", description = "유저의 알림 여부를 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "유저 정보 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping
    public ResponseEntity<?> updateMarketingConsent(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "UpdateMarketionConsentReq Schema를 확인해주세요.", required = true) @RequestBody UpdateMarketingConsentReq updateMarketingConsentReq
    ) {
        return userService.updateMarketingConsent(userPrincipal, updateMarketingConsentReq);
    }

    @Operation(summary = "유저 비건 타입 변경", description = "유저의 비건 타입을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 비건 타입 변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "유저 비건 타입 변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/vegan-type")
    public ResponseEntity<?> updateVeganType(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "UpdateVeganTypeReq Schema를 확인해주세요", required = true) @RequestBody UpdateVeganTypeReq updateVeganTypeReq
            ) {
        return userService.updateVeganType(userPrincipal, updateVeganTypeReq);
    }

    @Operation(summary = "유저 닉네임 변경", description = "유저의 닉네임을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 닉네임 변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "유저 닉네임 변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/nickname")
    public ResponseEntity<?> updateNickname(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "UpdateVeganTypeReq Schema를 확인해주세요", required = true) @RequestBody UpdateNicknameReq updateNicknameReq
    ) {
        return userService.updateNickname(userPrincipal, updateNicknameReq);
    }

}

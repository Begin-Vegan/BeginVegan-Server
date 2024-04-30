package com.beginvegan.domain.suggestion.presentation;

import com.beginvegan.domain.suggestion.application.ModificationSuggestionService;
import com.beginvegan.domain.suggestion.dto.request.ModifyReq;
import com.beginvegan.domain.suggestion.dto.request.RegistReq;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Bookmarks", description = "Bookmarks API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suggestions")
public class ModificationSuggestionController {

    private final ModificationSuggestionService modificationSuggestionService;

    @Operation(summary = "식당 정보 수정 제보 생성", description = "식당 정보 수정 제보를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제보 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "제보 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/modification")
    public ResponseEntity<?> createModificationSuggestion(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "ModifyReq를 참고해주세요.", required = true) @RequestBody ModifyReq modifyReq
    ) {
        return modificationSuggestionService.createModificationSuggestion(userPrincipal, modifyReq);
    }

}

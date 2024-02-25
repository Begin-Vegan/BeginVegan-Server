package com.beginvegan.domain.bookmark.presentation;

import com.beginvegan.domain.bookmark.application.BookmarkService;
import com.beginvegan.domain.bookmark.dto.BookmarkListRes;
import com.beginvegan.domain.food.dto.response.FoodRecipeListRes;
import com.beginvegan.global.config.security.token.CurrentUser;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Bookmarks", description = "Bookmarks API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "유저의 스크랩 조희", description = "유저의 스크랩을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 스크랩 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BookmarkListRes.class)) } ),
            @ApiResponse(responseCode = "400", description = "유저 스크랩 목록 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping
    public ResponseEntity<?> findBookmarksByUser(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "유저의 스크랩 목록을 페이지별로 조회합니다. **Page는 0부터 시작합니다!**", required = true) @RequestParam(value = "page") Integer page
    ) {
        return bookmarkService.findBookmarksByUser(userPrincipal, page);
    }
}

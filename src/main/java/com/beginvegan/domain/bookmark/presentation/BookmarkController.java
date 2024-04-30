package com.beginvegan.domain.bookmark.presentation;

import com.beginvegan.domain.bookmark.application.BookmarkService;
import com.beginvegan.domain.bookmark.dto.request.BookmarkReq;
import com.beginvegan.domain.bookmark.dto.response.BookmarkListRes;
import com.beginvegan.domain.food.dto.response.BookmarkFoodRes;
import com.beginvegan.domain.restaurant.dto.response.BookmarkRestaurantRes;
import com.beginvegan.domain.restaurant.dto.response.RandomRestaurantRes;
import com.beginvegan.global.config.security.token.CurrentUser;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ErrorResponse;
import com.beginvegan.global.payload.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bookmarks", description = "Bookmarks API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "스크랩 생성", description = "식당 / 매거진 / 레시피를 스크랩합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "스크랩 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "스크랩 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping
    public ResponseEntity<?> createBookmark(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "BookmarkReq를 참고해주세요.", required = true) @RequestBody BookmarkReq bookmarkReq
    ) {
        return bookmarkService.createBookmark(userPrincipal, bookmarkReq);
    }

    @Operation(summary = "스크랩 해제", description = "식당 / 매거진 / 레시피를 스크랩 해제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스크랩 해제 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "스크랩 해제 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping
    public ResponseEntity<?> deleteBookmark(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "BookmarkReq를 참고해주세요.", required = true) @RequestBody BookmarkReq bookmarkReq
    ) {
        return bookmarkService.deleteBookmark(userPrincipal, bookmarkReq);
    }

    // ------------ 북마크 조회 ------------
    // Description : 식당
    @Operation(summary = "유저가 스크랩한 식당 목록 조회", description = "유저가 스크랩한 식당 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스크랩 식당 목록 조회 성공", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookmarkRestaurantRes.class)))}),
            @ApiResponse(responseCode = "400", description = "스크랩 식당 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/restaurant")
    public ResponseEntity<?> findBookmarkRestaurant(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return bookmarkService.findBookmarkRestaurant(userPrincipal);
    }

    // Description : 레시피
    @Operation(summary = "유저가 스크랩한 레시피 목록 조회", description = "유저가 스크랩한 레시피 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스크랩 레시피 목록 조회 성공", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookmarkFoodRes.class)))}),
            @ApiResponse(responseCode = "400", description = "스크랩 레시피 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/recipe")
    public ResponseEntity<?> findBookmarkRecipe(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return bookmarkService.findBookmarkRecipe(userPrincipal);
    }

    // Description : 매거진
}

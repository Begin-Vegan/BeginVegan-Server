package com.beginvegan.domain.review.presentation;

import com.beginvegan.domain.report.dto.ReportContentReq;
import com.beginvegan.domain.review.application.ReviewService;
import com.beginvegan.domain.review.dto.request.PostReviewReq;
import com.beginvegan.domain.review.dto.request.UpdateReviewReq;
import com.beginvegan.domain.review.dto.response.MyReviewRes;
import com.beginvegan.domain.review.dto.response.RestaurantInfoRes;
import com.beginvegan.domain.review.dto.response.ReviewListRes;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Tag(name = "Reviews", description = "Reviews API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "유저의 리뷰 조희", description = "유저의 리뷰들을 최신순으로 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 리뷰 목록 조회 성공", content = {@Content(mediaType = "application/json", array = @ArraySchema( schema = @Schema(implementation = MyReviewRes.class))) } ),
            @ApiResponse(responseCode = "400", description = "유저 리뷰 목록 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping
    public ResponseEntity<?> findReviewsByUser(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "유저의 리뷰 목록을 페이지별로 조회합니다. **Page는 0부터 시작합니다!**", required = true) @RequestParam(value = "page") Integer page
    ) {
        return reviewService.findReviewsByUser(userPrincipal, page);
    }

    @Operation(summary = "리뷰 등록", description = "리뷰를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 등록 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)) } ),
            @ApiResponse(responseCode = "400", description = "리뷰 등록 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping
    public ResponseEntity<?> postReview(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "PostReviewReq Schema를 확인해주세요.", required = true) @Valid @RequestPart PostReviewReq postReviewReq,
            @Parameter(description = "form-data 형식의 Multipart-file을 입력해주세요.") @RequestPart Optional<MultipartFile[]> files

    ) {
        return reviewService.postReview(userPrincipal, postReviewReq, files);
    }

    @Operation(summary = "식당 정보 조회", description = "리뷰 작성 시 식당 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantInfoRes.class)) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<?> getRestaurantInfo(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "식당 id를 입력해주세요..", required = true) @PathVariable Long restaurantId
    ) {
        return reviewService.getRestaurantInfoForReview(userPrincipal, restaurantId);
    }

    @Operation(summary = "리뷰 조회", description = "리뷰를 조회합니다.")
    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReview(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "리뷰 id를 입력해주세요..", required = true) @PathVariable Long reviewId
    ) {
        return reviewService.getReviewInfo(userPrincipal, reviewId);
    }

    @Operation(summary = "리뷰 삭제", description = "내가 작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "리뷰 id를 입력해주세요..", required = true) @PathVariable Long reviewId
    ) {
        return reviewService.deleteReview(userPrincipal, reviewId);
    }

    @Operation(summary = "리뷰 수정", description = "내가 작성한 리뷰를 수정합니다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "리뷰 id를 입력해주세요..", required = true) @PathVariable Long reviewId,
            @Parameter(description = "UpdateReviewReq Schema를 확인해주세요.", required = true) @Valid @RequestPart UpdateReviewReq updateReviewReq,
            @Parameter(description = "form-data 형식의 Multipart-file을 입력해주세요.") @RequestPart Optional<MultipartFile[]> files
    ) {
        return reviewService.updateReview(userPrincipal, reviewId, updateReviewReq, files);
    }

    @Operation(summary = "리뷰 추천 및 추천 취소", description = "유저의 리뷰 추천 상태에 따라 리뷰를 추천하거나 추천을 취소합니다.")
    @PostMapping("/{reviewId}/recommendation")
    public ResponseEntity<?> recommendReview(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "리뷰 id를 입력해주세요..", required = true) @PathVariable Long reviewId
    ) {
        return reviewService.recommendReviews(userPrincipal, reviewId);
    }

    @Operation(summary = "리뷰 신고", description = "부적절한 리뷰를 신고합니다.")
    @PostMapping("/{reviewId}/report")
    public ResponseEntity<?> deleteReview(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "리뷰 id를 입력해주세요..", required = true) @PathVariable Long reviewId,
            @Parameter(description = "리뷰 신고 사유입니다.", required = true) @Valid @RequestBody ReportContentReq reportContentReq
            ) {
        return reviewService.reportReview(userPrincipal, reviewId, reportContentReq);
    }

}

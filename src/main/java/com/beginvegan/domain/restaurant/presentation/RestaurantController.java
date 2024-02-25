package com.beginvegan.domain.restaurant.presentation;

import com.beginvegan.domain.restaurant.application.RestaurantService;
import com.beginvegan.domain.restaurant.dto.AroundRestaurantListRes;
import com.beginvegan.domain.restaurant.dto.RestaurantAndMenusRes;
import com.beginvegan.domain.restaurant.dto.request.LocationReq;
import com.beginvegan.domain.review.dto.ReviewListRes;
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

@Tag(name = "Restaurants", description = "Restaurants API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(summary = "식당/카페 상세 정보(메뉴까지) 조희", description = "식당/카페 상세 정보(메뉴까지)를 조희합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식당/카페 상세 정보(메뉴까지) 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantAndMenusRes.class))}),
            @ApiResponse(responseCode = "400", description = "식당/카페 상세 정보(메뉴까지) 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{restaurant-id}")
    public ResponseEntity<?> findRestaurantById(
            @Parameter(description = "식당/카페를 ID로 조회합니다.", required = true) @PathVariable(value = "restaurant-id") Long restaurantId
    ) {
        return restaurantService.findRestaurantById(restaurantId);
    }

    @Operation(summary = "식당/카페 리뷰 조희", description = "식당/카페 리뷰를 조희합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식당/카페 리뷰 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReviewListRes.class))}),
            @ApiResponse(responseCode = "400", description = "식당/카페 리뷰 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/review/{restaurant-id}")
    public ResponseEntity<?> findRestaurantReviewsById(
            @Parameter(description = "식당/카페ID로 리뷰를 조회합니다.", required = true) @PathVariable(value = "restaurant-id") Long restaurantId,
            @Parameter(description = "식당/카페의 리뷰 목록을 페이지별로 조회합니다. **Page는 0부터 시작합니다!**", required = true) @RequestParam(value = "page") Integer page
    ) {
        return restaurantService.findRestaurantReviewsById(restaurantId, page);
    }

    @Operation(summary = "식당/카페 스크랩", description = "식당/카페를 스크랩합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식당/카페 스크랩 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "식당/카페 스크랩 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/{restaurant-id}")
    public ResponseEntity<?> scrapRestaurant(
            @Parameter(description = "AccessToken을 입력해주세요") @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "식당/카페ID로 스크랩합니다.", required = true) @PathVariable(value = "restaurant-id") Long restaurantId
    ) {
        return restaurantService.scrapRestaurant(userPrincipal, restaurantId);
    }

    @Operation(summary = "식당/카페 스크랩 해제", description = "식당/카페를 스크랩을 해제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식당/카페 스크랩 해제 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "식당/카페 스크랩 해제 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping("/{restaurant-id}")
    public ResponseEntity<?> deleteScrapRestaurant(
            @Parameter(description = "AccessToken을 입력해주세요") @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "식당/카페ID로 스크랩합니다.", required = true) @PathVariable(value = "restaurant-id") Long restaurantId
    ) {
        return restaurantService.deleteScrapRestaurant(userPrincipal, restaurantId);
    }

    @Operation(summary = "주변 식당/카페 리스트 조회", description = "주변 식당/카페 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주변 식당/카페 리스트 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AroundRestaurantListRes.class))}),
            @ApiResponse(responseCode = "400", description = "주변 식당/카페 리스트 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/around")
    public ResponseEntity<?> findAroundRestaurant(@RequestBody LocationReq locationReq) {
        return restaurantService.findAroundRestaurant(locationReq);
    }

}

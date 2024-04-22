package com.beginvegan.domain.restaurant.presentation;

import com.beginvegan.domain.restaurant.application.RestaurantService;
import com.beginvegan.domain.restaurant.dto.response.AroundRestaurantListRes;
import com.beginvegan.domain.restaurant.dto.response.RandomRestaurantRes;
import com.beginvegan.domain.restaurant.dto.response.RestaurantAndMenusRes;
import com.beginvegan.domain.restaurant.dto.request.LocationReq;
import com.beginvegan.domain.restaurant.dto.response.RestaurantBannerRes;
import com.beginvegan.domain.review.dto.ReviewListRes;
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

@Tag(name = "Restaurants", description = "Restaurants API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 식당 상세 조회 (메뉴 포함)
    @Operation(summary = "식당/카페 상세 정보(메뉴까지) 조희", description = "식당/카페 상세 정보(메뉴까지)를 조희합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식당/카페 상세 정보(메뉴까지) 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantAndMenusRes.class))}),
            @ApiResponse(responseCode = "400", description = "식당/카페 상세 정보(메뉴까지) 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{restaurant-id}")
    public ResponseEntity<?> findRestaurantById(
            @Parameter(description = "AccessToken을 입력해주세요") @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "식당/카페를 ID로 조회합니다.", required = true) @PathVariable(value = "restaurant-id") Long restaurantId,
            @Parameter(description = "LocationReq를 참고해주세요.", required = true) @RequestBody LocationReq locationReq
    ) {
        return restaurantService.findRestaurantById(userPrincipal, restaurantId, locationReq);
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

    // -------------- 새로운 비긴 비건 --------------

    // 권한 x - 랜덤 식당 3개 조회
    @Operation(summary = "홈 화면 - 위치 권한 x, 랜덤 식당 3개 조희", description = "홈 화면에서 사용될 위치 권한 미동의 시 랜덤 식당 3개를 조희합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RandomRestaurantRes.class)))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/random/{count}")
    public ResponseEntity<?> findRandomRestaurant(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal, // 스크랩 여부 확인 필요
            @Parameter(description = "랜덤 조회할 개수를 입력해주세요. (기본 3개)", required = true) @PathVariable(value = "count") Long count
    ) {
        return restaurantService.findRandomRestaurant(userPrincipal, count);
    }

    // 권한 o - 주변 10km 이내 랜덤 식당 3개 조회
    @Operation(summary = "홈 화면 - 위치 권한 o, 10km 이내 랜덤 식당 3개 조희", description = "홈 화면에서 사용될 위치 권한 동의 시 10km 이내 랜덤 식당 3개를 조희합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RandomRestaurantRes.class)))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/random/permission/{count}")
    public ResponseEntity<?> findRandomRestaurantWithPermission(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal, // 스크랩 여부 확인 필요
            @Parameter(description = "랜덤 조회할 개수를 입력해주세요. (기본 3개)", required = true) @PathVariable(value = "count") Long count,
            @Parameter(description = "LocationReq를 참고해주세요.", required = true) @RequestBody LocationReq locationReq
    ) {
        return restaurantService.findRandomRestaurantWithPermission(userPrincipal, count, locationReq);
    }

    // Map 1depth - 식당 리스트 조회 : 가까운 순
    @Operation(summary = "식당 리스트 가까운 순 조회", description = "식당 리스트 가까운 순 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantBannerRes.class)))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/around")
    public ResponseEntity<?> findAroundRestaurantList(
            @Parameter(description = "LocationReq를 참고해주세요.", required = true) @RequestBody LocationReq locationReq,
            @Parameter(description = "식당 리스트를 페이지별로 가까운 순 조회합니다. **Page는 0부터 시작합니다!**", required = true) @RequestParam(value = "page") Integer page

    ) {
        return restaurantService.findAroundRestaurantList(locationReq, page);
    }
}

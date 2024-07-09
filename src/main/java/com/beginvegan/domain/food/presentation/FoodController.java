package com.beginvegan.domain.food.presentation;

import com.beginvegan.domain.food.application.FoodService;
import com.beginvegan.domain.food.dto.response.FoodRecipeListRes;
import com.beginvegan.domain.food.dto.response.FoodDetailRes;
import com.beginvegan.domain.food.dto.response.FoodListRes;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "Foods", description = "Foods API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/foods")
public class FoodController {

    private final FoodService foodService;

    @Operation(summary = "전체 레시피 목록 조회", description = "레시피 탭에서 사용할 정보를 포함한 전체 레시피 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 레시피 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FoodRecipeListRes.class)) } ),
            @ApiResponse(responseCode = "400", description = "전체 레시피 목록 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("")
    public ResponseEntity<?> findAllFoods(
            @Parameter(description = "레시피 리스트를 조회합니다. **Page는 0부터 시작합니다!**", required = true) @RequestParam(value = "page") Integer page,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return foodService.findAllFoods(userPrincipal, page);
    }

    // 레시피 상세 정보 조회
    @Operation(summary = "레시피 상세 정보 조회", description = "food_id를 통한 레시피 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레시피 상세 정보 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FoodDetailRes.class)) } ),
            @ApiResponse(responseCode = "400", description = "레시피 상세 정보 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findFoodDetail(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return foodService.findFoodDetail(userPrincipal, id);
    }

    // 랜덤 음식 3가지 조회
    @Operation(summary = "3가지 음식 목록 조회", description = "3가지 음식 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "3가지 음식 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FoodListRes.class)) } ),
            @ApiResponse(responseCode = "400", description = "3가지 음식 목록 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/home/recipe")
    public ResponseEntity<?> findThreeFoods(@CurrentUser UserPrincipal userPrincipal){
        return foodService.findThreeFoods(userPrincipal);
    }

    @Operation(summary = "나를 위한 레시피 조회", description = "나의 비건 타입에 맞는 레시피 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "나를 위한 레시피 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FoodRecipeListRes.class)) } ),
            @ApiResponse(responseCode = "400", description = "나를 위한 레시피 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = FoodRecipeListRes.class) ) } ),
    })
    @GetMapping("/my")
    public ResponseEntity<?> findMyFoods(
            @Parameter(description = "나의 비건 타입에 맞는 레시피 리스트를 조회합니다. **Page는 0부터 시작합니다!**", required = true) @RequestParam(value = "page") Integer page,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return foodService.findMyFoods(page, userPrincipal);
    }

}

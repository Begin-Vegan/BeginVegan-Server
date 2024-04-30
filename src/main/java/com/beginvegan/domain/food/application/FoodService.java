package com.beginvegan.domain.food.application;

import com.beginvegan.domain.block.dto.BlockDto;
import com.beginvegan.domain.food.domain.Food;
import com.beginvegan.domain.food.domain.repository.FoodRepository;
import com.beginvegan.domain.food.dto.FoodIngredientDto;
import com.beginvegan.domain.food.dto.response.FoodRecipeListRes;
import com.beginvegan.domain.food.dto.request.FoodDetailReq;
import com.beginvegan.domain.food.dto.response.FoodDetailRes;
import com.beginvegan.domain.food.dto.response.FoodListRes;
import com.beginvegan.domain.food.exception.FoodNotFoundException;
import com.beginvegan.global.DefaultAssert;
import com.beginvegan.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FoodService {

    private final FoodRepository foodRepository;

    // 레시피 전체 조회 : 재료 포함 :: 하단 바 레시피 클릭 시 화면
    public ResponseEntity<?> findAllFoodsWithIngredients() {
        List<Food> foods = foodRepository.findAll();
        List<FoodRecipeListRes> foodDtos = new ArrayList<>();

        for (Food food : foods) {
            List<FoodIngredientDto> foodIngredientDtos = food.getIngredients().stream()
                    .map(ingredient -> FoodIngredientDto.builder()
                            .id(ingredient.getId())
                            .name(ingredient.getName())
                            .build())
                    .collect(Collectors.toList());

            FoodRecipeListRes foodRecipeListRes = FoodRecipeListRes.builder()
                    .id(food.getId())
                    .name(food.getName())
                    .veganType(food.getVeganType())
                    .ingredients(foodIngredientDtos)
                    .build();

            foodDtos.add(foodRecipeListRes);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(foodDtos)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // food_id를 통한 레시피 검색
    public ResponseEntity<?> findFoodDetail(FoodDetailReq foodDetailReq) {
        Optional<Food> foodOptional = foodRepository.findById(foodDetailReq.getId());
        Food food = foodOptional.orElseThrow(() -> new FoodNotFoundException("해당 아이디를 가진 음식을 찾을 수 없습니다. ID: " + foodDetailReq.getId()));

        List<FoodIngredientDto> ingredientDtos = food.getIngredients().stream()
                .map(ingredient -> FoodIngredientDto.builder()
                        .id(ingredient.getId())
                        .name(ingredient.getName())
                        .build())
                .collect(Collectors.toList());

        List<BlockDto> blockDtos = food.getFoodBlocks().stream()
                .map(block -> BlockDto.builder()
                        .id(block.getId())
                        .content(block.getContent())
                        .sequence(block.getSequence())
                        .build())
                .sorted(Comparator.comparing(BlockDto::getSequence))
                .collect(Collectors.toList());

        FoodDetailRes foodDetailRes = FoodDetailRes.builder()
                .id(food.getId())
                .name(food.getName())
                .veganType(food.getVeganType())
                .ingredients(ingredientDtos)
                .blocks(blockDtos)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(foodDetailRes)
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    // 3가지 음식 랜덤 조회 : 메인 페이지
    public ResponseEntity<?> findThreeFoods() {
        List<Food> foods = foodRepository.findAll();
        List<FoodListRes> foodList = new ArrayList<>();

        // 랜덤 수 3개 추리기
        Set<Integer> randomNum = new HashSet<>();
        while(randomNum.size() < 3){
            randomNum.add((int)(Math.random() * foods.size()));
        }

        Iterator<Integer> iter = randomNum.iterator();
        while(iter.hasNext()){
            int num = iter.next();
            FoodListRes foodListRes = FoodListRes.builder()
                    .id(foods.get(num).getId())
                    .name(foods.get(num).getName())
                    .veganType(foods.get(num).getVeganType())
                    .build();
            foodList.add(foodListRes);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(foodList)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    public Food validateFoodById(Long foodId) {
        Optional<Food> findFood = foodRepository.findById(foodId);
        DefaultAssert.isTrue(findFood.isPresent(), "잘못된 레시피 아이디입니다.");
        return findFood.get();
    }

}
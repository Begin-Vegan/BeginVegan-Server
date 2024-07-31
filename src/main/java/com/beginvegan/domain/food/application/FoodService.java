package com.beginvegan.domain.food.application;

import com.beginvegan.domain.block.dto.BlockDto;
import com.beginvegan.domain.bookmark.domain.repository.BookmarkRepository;
import com.beginvegan.domain.bookmark.domain.repository.ContentType;
import com.beginvegan.domain.food.domain.Food;
import com.beginvegan.domain.food.domain.repository.FoodRepository;
import com.beginvegan.domain.food.dto.FoodIngredientDto;
import com.beginvegan.domain.food.dto.response.FoodRecipeListRes;
import com.beginvegan.domain.food.dto.response.FoodDetailRes;
import com.beginvegan.domain.food.dto.response.FoodListRes;
import com.beginvegan.domain.food.exception.FoodNotFoundException;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.VeganType;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.global.DefaultAssert;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final UserRepository userRepository;
    private final UserService userService;
    private final BookmarkRepository bookmarkRepository;

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
    public ResponseEntity<?> findFoodDetail(UserPrincipal userPrincipal, Long foodId) {
        Optional<Food> foodOptional = foodRepository.findById(foodId);
        Food food = foodOptional.orElseThrow(() -> new FoodNotFoundException("해당 아이디를 가진 음식을 찾을 수 없습니다. ID: " + foodId));

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
                .isBookmarked(isBookMarked(userPrincipal, food.getId()))
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(foodDetailRes)
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    // 3가지 음식 랜덤 조회 : 메인 페이지
    public ResponseEntity<?> findThreeFoods(UserPrincipal userPrincipal) {
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
                    .isBookmarked(isBookMarked(userPrincipal, foods.get(num).getId()))
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

    public ResponseEntity<?> findAllFoods(UserPrincipal userPrincipal, Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<Food> foods = foodRepository.findAll(pageable).getContent();
        List<FoodListRes> foodList = new ArrayList<>();

        for (Food food : foods) {
            FoodListRes foodListRes = FoodListRes.builder()
                    .id(food.getId())
                    .name(food.getName())
                    .veganType(food.getVeganType())
                    .isBookmarked(isBookMarked(userPrincipal, food.getId()))
                    .build();
            foodList.add(foodListRes);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(foodList)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> findMyFoods(Integer page, UserPrincipal userPrincipal) {
        Pageable pageable = PageRequest.of(page, 10);
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        VeganType myVeganType = user.getVeganType();

        //VeganType myVeganType = VeganType.UNKNOWN;

        // 사용자의 채식 성향보다 덜 엄격한 모든 채식 성향을 가져옵니다.
        List<VeganType> veganTypes = getVeganTypes(myVeganType);

        // 해당 채식 성향 리스트에 맞는 모든 음식을 가져옵니다.
        Page<Food> foodPage = foodRepository.findAllByVeganTypeIn(veganTypes, pageable);

        // 추가 필터링: LactoVegetarian인 경우 OvoVegetarian 음식을 제외, OvoVegetarian인 경우 LactoVegetarian 음식을 제외
        List<Food> filteredFoods = foodPage.stream()
                .filter(food -> !((myVeganType == VeganType.LACTO_VEGETARIAN && food.getVeganType() == VeganType.OVO_VEGETARIAN) ||
                        (myVeganType == VeganType.OVO_VEGETARIAN && food.getVeganType() == VeganType.LACTO_VEGETARIAN)))
                .collect(Collectors.toList());

        List<FoodListRes> foodList = filteredFoods.stream()
                .map(food -> FoodListRes.builder()
                        .id(food.getId())
                        .name(food.getName())
                        .veganType(food.getVeganType())
                        .isBookmarked(isBookMarked(userPrincipal, food.getId()))
                        .build())
                .collect(Collectors.toList());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(foodList)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 사용자의 채식 성향보다 덜 엄격한 모든 채식 성향을 반환하는 헬퍼 메소드
    private List<VeganType> getVeganTypes(VeganType myVeganType) {
        List<VeganType> veganTypes = new ArrayList<>();
        for (VeganType type : VeganType.values()) {
            if (type.getOrder() <= myVeganType.getOrder()) {
                veganTypes.add(type);
            }
        }
        return veganTypes;
    }

    private Boolean isBookMarked(UserPrincipal userPrincipal, Long magazineId) {
        User user = userService.validateUserById(userPrincipal.getId());
        Boolean isBookmarked = bookmarkRepository.existsByUserAndContentIdAndContentType(user, magazineId, ContentType.RECIPE);
        if(isBookmarked == null) {
            isBookmarked = false;
        }
        return isBookmarked;
    }


}
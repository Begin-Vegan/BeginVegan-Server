package com.beginvegan.domain.suggestion.application;

import com.beginvegan.domain.restaurant.application.RestaurantService;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.suggestion.domain.child.ModificationSuggestion;
import com.beginvegan.domain.suggestion.domain.child.repository.ModificationSuggestionRepository;
import com.beginvegan.domain.suggestion.domain.parent.Inspection;
import com.beginvegan.domain.suggestion.domain.parent.SuggestionType;
import com.beginvegan.domain.suggestion.dto.request.ModifyReq;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ApiResponse;
import com.beginvegan.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModificationSuggestionService {

    private final ModificationSuggestionRepository modificationSuggestionRepository;

    private final UserService userService;
    private final RestaurantService restaurantService;

    @Transactional
    public ResponseEntity<?> createModificationSuggestion(UserPrincipal userPrincipal, ModifyReq modifyReq) {

        User user = userService.validateUserById(userPrincipal.getId());
        Restaurant restaurant = restaurantService.validateRestaurantById(modifyReq.getRestaurantId());

        ModificationSuggestion modificationSuggestion = ModificationSuggestion.builder()
                .user(user)
                .suggestionType(SuggestionType.MODIFICATION)
                .inspection(Inspection.INCOMPLETE)
                .restaurant(restaurant)
                .content(modifyReq.getContent())
                .build();

        modificationSuggestionRepository.save(modificationSuggestion);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("식당 정보 수정 제보가 완료되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}

package com.beginvegan.domain.suggestion.application;

import com.beginvegan.domain.suggestion.domain.child.RegistrationSuggestion;
import com.beginvegan.domain.suggestion.domain.child.repository.RegistrationSuggestionRepository;
import com.beginvegan.domain.suggestion.domain.parent.Inspection;
import com.beginvegan.domain.suggestion.domain.parent.SuggestionType;
import com.beginvegan.domain.suggestion.dto.request.RegistReq;
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
public class RegistrationSuggestionService {

    private final RegistrationSuggestionRepository registrationSuggestionRepository;

    private final UserService userService;

    @Transactional
    public ResponseEntity<?> createRegistrationSuggestion(UserPrincipal userPrincipal, RegistReq registReq) {

        User user = userService.validateUserById(userPrincipal.getId());

        RegistrationSuggestion registrationSuggestion = RegistrationSuggestion.builder()
                .user(user)
                .suggestionType(SuggestionType.REGISTRATION)
                .inspection(Inspection.INCOMPLETE)
                .name(registReq.getName())
                .location(registReq.getLocation())
                .content(registReq.getContent())
                .build();

        registrationSuggestionRepository.save(registrationSuggestion);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("신규 식당 제보가 완료되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}

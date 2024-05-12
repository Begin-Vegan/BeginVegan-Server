package com.beginvegan.domain.suggestion.domain.child;

import com.beginvegan.domain.suggestion.domain.parent.Inspection;
import com.beginvegan.domain.suggestion.domain.parent.Suggestion;
import com.beginvegan.domain.suggestion.domain.parent.SuggestionType;
import com.beginvegan.domain.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RegistrationSuggestion extends Suggestion {

    private String name;

    private String location;

    @Lob
    private String content; // 식당 설명

    @Builder
    public RegistrationSuggestion(Long id, User user, SuggestionType suggestionType, Inspection inspection, String name, String location, String content) {
        super(id, user, suggestionType, inspection);
        this.name = name;
        this.location = location;
        this.content = content;
    }
}

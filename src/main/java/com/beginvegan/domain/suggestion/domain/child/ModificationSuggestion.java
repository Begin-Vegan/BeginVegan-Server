package com.beginvegan.domain.suggestion.domain.child;

import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.suggestion.domain.parent.Inspection;
import com.beginvegan.domain.suggestion.domain.parent.Suggestion;
import com.beginvegan.domain.suggestion.domain.parent.SuggestionType;
import com.beginvegan.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ModificationSuggestion extends Suggestion {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Lob
    private String content; // 제보 내용

    public ModificationSuggestion(Long id, User user, SuggestionType suggestionType, Inspection inspection, Restaurant restaurant, String content) {
        super(id, user, suggestionType, inspection);
        this.restaurant = restaurant;
        this.content = content;
    }
}

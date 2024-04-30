package com.beginvegan.domain.suggestion.domain.child.repository;

import com.beginvegan.domain.suggestion.domain.child.RegistrationSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationSuggestionRepository extends JpaRepository<RegistrationSuggestion, Long> {
}

package com.beginvegan.domain.suggestion.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Bookmarks", description = "Bookmarks API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suggestions")
public class ModificationSuggestionController {


    
}

package com.beginvegan.domain.bookmark.application;

import com.beginvegan.domain.bookmark.domain.Bookmark;
import com.beginvegan.domain.bookmark.domain.repository.BookmarkRepository;
import com.beginvegan.domain.bookmark.dto.BookmarkListRes;
import com.beginvegan.domain.restaurant.dto.RestaurantDetailRes;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.domain.user.exception.InvalidUserException;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> findBookmarksByUser(UserPrincipal userPrincipal, Integer page) {
//        User user = userRepository.findById(userPrincipal.getId())
//                .orElseThrow(InvalidUserException::new);
//
//        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
//        Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarksByUser(user, pageRequest);
//
//        List<Bookmark> bookmarks = bookmarkPage.getContent();
//        List<RestaurantDetailRes> restaurants = bookmarks.stream()
//                .map(bookmark -> RestaurantDetailRes.toDto(bookmark.getContentId()))
//                .toList();
//
//        BookmarkListRes bookmarkListRes = BookmarkListRes.builder()
//                .restaurants(restaurants)
//                .totalCount(bookmarkPage.getTotalElements())
//                .build();
//
//        ApiResponse apiResponse = ApiResponse.builder()
//                .check(true)
//                .information(bookmarkListRes)
//                .build();

//        return ResponseEntity.ok(apiResponse);
        return ResponseEntity.ok("엔티티 변경으로 인해 다시 구현해야 함");
    }

}

package com.beginvegan.domain.bookmark.application;

import com.beginvegan.domain.bookmark.domain.Bookmark;
import com.beginvegan.domain.bookmark.domain.repository.BookmarkRepository;
import com.beginvegan.domain.bookmark.domain.repository.ContentType;
import com.beginvegan.domain.bookmark.dto.request.BookmarkReq;
import com.beginvegan.domain.restaurant.application.RestaurantService;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.restaurant.dto.response.BookmarkRestaurantRes;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.global.DefaultAssert;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ApiResponse;
import com.beginvegan.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    private final RestaurantService restaurantService;
    private final UserService userService;

    @Transactional
    public ResponseEntity<?> createBookmark(UserPrincipal userPrincipal, BookmarkReq bookmarkReq) {

        User user = userService.validateUserById(userPrincipal.getId());

        boolean exist = bookmarkRepository.existsBookmarkByContentIdAndContentTypeAndUser(bookmarkReq.getContentId(), bookmarkReq.getContentType(), user);
        DefaultAssert.isTrue(!exist, "이미 스크랩한 상태입니다.");

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .contentId(bookmarkReq.getContentId())
                .contentType(bookmarkReq.getContentType())
                .build();
        bookmarkRepository.save(bookmark);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("스크랩 되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> deleteBookmark(UserPrincipal userPrincipal, BookmarkReq bookmarkReq) {

        User user = userService.validateUserById(userPrincipal.getId());

        Optional<Bookmark> findBookmark = bookmarkRepository.findByContentIdAndContentTypeAndUser(bookmarkReq.getContentId(), bookmarkReq.getContentType(), user);
        DefaultAssert.isTrue(findBookmark.isPresent(), "스크랩 되어 있지 않습니다.");

        Bookmark bookmark = findBookmark.get();
        bookmarkRepository.delete(bookmark);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("스크랩 해제를 완료했습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // Description : 북마크한 식당 목록 조회
    public ResponseEntity<?> findBookmarkRestaurant(UserPrincipal userPrincipal) {

        User user = userService.validateUserById(userPrincipal.getId());
        List<Bookmark> bookmarks = bookmarkRepository.findByContentTypeAndUser(ContentType.RESTAURANT, user);
        List<BookmarkRestaurantRes> bookmarkRestaurantResList = new ArrayList<>();

        for (Bookmark bookmark : bookmarks) {
            Long restaurantId = bookmark.getContentId();
            Restaurant restaurant = restaurantService.validateRestaurantById(restaurantId);

            BookmarkRestaurantRes bookmarkRestaurantRes = BookmarkRestaurantRes.builder()
                    .restaurantId(bookmark.getContentId())
                    .thumbnail(restaurant.getThumbnail())
                    .name(restaurant.getName())
                    .restaurantType(restaurant.getRestaurantType())
                    .address(restaurant.getAddress())
                    .build();
            bookmarkRestaurantResList.add(bookmarkRestaurantRes);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(bookmarkRestaurantResList)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}

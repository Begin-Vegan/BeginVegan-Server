package com.beginvegan.domain.bookmark.application;

import com.beginvegan.domain.bookmark.domain.Bookmark;
import com.beginvegan.domain.bookmark.domain.repository.BookmarkRepository;
import com.beginvegan.domain.bookmark.domain.repository.ContentType;
import com.beginvegan.domain.bookmark.dto.request.BookmarkReq;
import com.beginvegan.domain.food.application.FoodService;
import com.beginvegan.domain.food.domain.Food;
import com.beginvegan.domain.food.dto.response.BookmarkFoodRes;
import com.beginvegan.domain.magazine.application.MagazineService;
import com.beginvegan.domain.magazine.domain.Magazine;
import com.beginvegan.domain.magazine.dto.response.BookmarkMagazineRes;
import com.beginvegan.domain.restaurant.application.RestaurantService;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.restaurant.dto.request.LocationReq;
import com.beginvegan.domain.restaurant.dto.response.BookmarkRestaurantRes;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.global.DefaultAssert;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ApiResponse;
import com.beginvegan.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final FoodService foodService;
    private final MagazineService magazineService;
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
    public ResponseEntity<?> findBookmarkRestaurant(UserPrincipal userPrincipal, Integer page, LocationReq locationReq) {

        User user = userService.validateUserById(userPrincipal.getId());

        PageRequest pageRequest = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarksByContentTypeAndUser(ContentType.RESTAURANT, user, pageRequest);

        List<BookmarkRestaurantRes> bookmarkRestaurantResList = new ArrayList<>();
        double userLatitude = Double.parseDouble(locationReq.getLatitude());
        double userLongitude = Double.parseDouble(locationReq.getLongitude());

        for (Bookmark bookmark : bookmarkPage) {
            Long restaurantId = bookmark.getContentId();
            Restaurant restaurant = restaurantService.validateRestaurantById(restaurantId);

            double restaurantLatitude = Double.parseDouble(restaurant.getLatitude());
            double restaurantLongitude = Double.parseDouble(restaurant.getLongitude());

            double distance = restaurantService.calculateDistance(userLatitude, userLongitude, restaurantLatitude, restaurantLongitude);

            BookmarkRestaurantRes bookmarkRestaurantRes = BookmarkRestaurantRes.builder()
                    .restaurantId(bookmark.getContentId())
                    .thumbnail(restaurant.getThumbnail())
                    .name(restaurant.getName())
                    .restaurantType(restaurant.getRestaurantType())
                    .rate(restaurant.getRate())
                    .distance(distance)
                    .build();
            bookmarkRestaurantResList.add(bookmarkRestaurantRes);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(bookmarkRestaurantResList)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // Description : 북마크한 레시피 목록 조회
    public ResponseEntity<?> findBookmarkRecipe(UserPrincipal userPrincipal, Integer page) {

        User user = userService.validateUserById(userPrincipal.getId());

        PageRequest pageRequest = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarksByContentTypeAndUser(ContentType.RECIPE, user, pageRequest);

        List<BookmarkFoodRes> bookmarkFoodResList = new ArrayList<>();

        for (Bookmark bookmark : bookmarkPage) {
            Long foodId = bookmark.getContentId();
            Food food = foodService.validateFoodById(foodId);

            BookmarkFoodRes bookmarkFoodRes = BookmarkFoodRes.builder()
                    .foodId(foodId)
                    .name(food.getName())
                    .veganType(food.getVeganType())
                    .build();

            bookmarkFoodResList.add(bookmarkFoodRes);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(bookmarkFoodResList)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // Description : 북마크한 매거진 목록 조회
    public ResponseEntity<?> findBookmarkMagazine(UserPrincipal userPrincipal, Integer page) {

        User user = userService.validateUserById(userPrincipal.getId());

        PageRequest pageRequest = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Bookmark> bookmarkPage = bookmarkRepository.findBookmarksByContentTypeAndUser(ContentType.MAGAZINE, user, pageRequest);

        List<BookmarkMagazineRes> bookmarkMagazineResList = new ArrayList<>();

        for (Bookmark bookmark : bookmarkPage) {
            Long magazineId = bookmark.getContentId();
            Magazine magazine = magazineService.validateMagazineById(magazineId);

            BookmarkMagazineRes bookmarkMagazineRes = BookmarkMagazineRes.builder()
                    .magazineId(magazineId)
                    .thumbnail(magazine.getThumbnail())
                    .title(magazine.getTitle())
                    .writeTime(magazine.getCreatedDate().toLocalDate())
                    .editor(magazine.getEditor())
                    .build();

            bookmarkMagazineResList.add(bookmarkMagazineRes);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(bookmarkMagazineResList)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}

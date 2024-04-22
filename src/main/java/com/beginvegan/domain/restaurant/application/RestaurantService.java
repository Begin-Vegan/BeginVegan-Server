package com.beginvegan.domain.restaurant.application;

import com.beginvegan.domain.bookmark.domain.Bookmark;
import com.beginvegan.domain.bookmark.domain.repository.BookmarkRepository;
import com.beginvegan.domain.bookmark.domain.repository.ContentType;
import com.beginvegan.domain.food.domain.Food;
import com.beginvegan.domain.food.dto.response.FoodListRes;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.restaurant.domain.repository.RestaurantRepository;
import com.beginvegan.domain.restaurant.dto.*;
import com.beginvegan.domain.restaurant.dto.request.LocationReq;
import com.beginvegan.domain.restaurant.dto.response.*;
import com.beginvegan.domain.restaurant.exception.InvalidRestaurantException;
import com.beginvegan.domain.review.domain.Review;
import com.beginvegan.domain.review.domain.repository.ReviewRepository;
import com.beginvegan.domain.review.dto.RestaurantReviewDetailRes;
import com.beginvegan.domain.review.dto.ReviewListRes;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.domain.user.exception.InvalidUserException;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ApiResponse;
import com.beginvegan.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    private final UserService userService;

    // 지구의 반지름
    private static final int EARTH_RADIUS = 6371;

    public ResponseEntity<?> findRestaurantById(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findRestaurantById(restaurantId)
                .orElseThrow(InvalidRestaurantException::new);

        RestaurantAndMenusRes restaurantAndMenusRes = RestaurantAndMenusRes.builder()
                .restaurant(RestaurantDetailRes.toDto(restaurant))
                .menus(restaurant.getMenus().stream()
                        .map(MenuDetailRes::toDto)
                        .toList())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(restaurantAndMenusRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> findRestaurantReviewsById(Long restaurantId, Integer page) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(InvalidRestaurantException::new);

        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "date"));
        Page<Review> reviewPage = reviewRepository.findReviewsByRestaurant(restaurant, pageRequest);

        List<Review> reviews = reviewPage.getContent();
        List<RestaurantReviewDetailRes> reviewDetailRes = reviews.stream()
                .map(review -> RestaurantReviewDetailRes.builder()
                        .id(review.getId())
                        .content(review.getContent())
                        .date(review.getDate())
                        .user(review.getUser())
                        .build())
                .toList();

        ReviewListRes reviewListRes = ReviewListRes.builder()
                .reviews(reviewDetailRes)
                .totalCount(reviewPage.getTotalElements())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(reviewListRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
    
    // TODO : 스크랩 변경사항 때문에 스크랩 로직 변경 필요 --------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public ResponseEntity<?> scrapRestaurant(UserPrincipal userPrincipal, Long restaurantId) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(InvalidRestaurantException::new);

//        if (bookmarkRepository.existsBookmarkByUserAndRestaurant(user, restaurant)) {
//            throw new ExistsBookmarkException();
//        }

        Bookmark bookmark = Bookmark.builder()
                .user(user)
//                .restaurant(restaurant)
                .build();

        bookmarkRepository.save(bookmark);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("스크랩 되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> deleteScrapRestaurant(UserPrincipal userPrincipal, Long restaurantId) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(InvalidRestaurantException::new);

//        if (!bookmarkRepository.existsBookmarkByUserAndRestaurant(user, restaurant)) {
//            throw new NotExistsBookmarkException();
//        }

//        Bookmark bookmark = bookmarkRepository.findBookmarkByUserAndRestaurant(user, restaurant);
//        bookmarkRepository.delete(bookmark);

//        ApiResponse apiResponse = ApiResponse.builder()
//                .check(true)
//                .information(Message.builder().message("스크랩 해제되었습니다.").build())
//                .build();

        return ResponseEntity.ok("엔티티 변경으로 인한 다시 구현");
    }

    public ResponseEntity<?> findAroundRestaurant(LocationReq locationReq) {

        List<Restaurant> nearRestaurants = restaurantRepository.findAllWithMenus();

        List<AroundRestaurantListRes> restaurantDtos = new ArrayList<>();

        double userLatitude = Double.parseDouble(locationReq.getLatitude());
        double userLongitude = Double.parseDouble(locationReq.getLongitude());

        if(!nearRestaurants.isEmpty()) {
            for (Restaurant nearRestaurant : nearRestaurants) {
                double restaurantLatitude = Double.parseDouble(nearRestaurant.getLatitude());
                double restaurantLongitude = Double.parseDouble(nearRestaurant.getLongitude());

                double distance = calculateDistance(userLatitude, userLongitude, restaurantLatitude, restaurantLongitude);

                // 5km 안에 있는 식당들만 포함
                if (distance <= 5) {
                    List<MenuDto> menuDtos = nearRestaurant.getMenus().stream()
                            .map(menu -> MenuDto.builder()
                                    .id(menu.getId())
//                                    .imageUrl(menu.getImageUrl())
                                    .build())
                            .collect(Collectors.toList());

                    AroundRestaurantListRes aroundRestaurantListRes = AroundRestaurantListRes.builder()
                            .id(nearRestaurant.getId())
                            .name(nearRestaurant.getName())
                            .address(nearRestaurant.getAddress())
                            .latitude(nearRestaurant.getLatitude())
                            .longitude(nearRestaurant.getLongitude())
//                            .imageUrl(nearRestaurant.getImageUrl())
                            .menus(menuDtos)
                            .build();

                    restaurantDtos.add(aroundRestaurantListRes);
                }
            }
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(restaurantDtos)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // -------------- 새로운 비긴 비건 --------------

    // home - 권한 동의 x, 랜덤 식당 3개 조회
    public ResponseEntity<?> findRandomRestaurant(UserPrincipal userPrincipal, Long count) {

        User user = userService.validateUserById(userPrincipal.getId());

        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<RandomRestaurantRes> restaurantResList = new ArrayList<>();

        // 랜덤 수 count개(3개) 추리기
        Set<Integer> randomNum = new HashSet<>();
        while(randomNum.size() < count){
            randomNum.add((int)(Math.random() * restaurants.size()));
        }

        Iterator<Integer> iter = randomNum.iterator();
        while(iter.hasNext()){
            int num = iter.next();
            Restaurant restaurant = restaurants.get(num);
            // 북마크 여부
            Optional<Bookmark> findBookmark = bookmarkRepository.findByContentIdAndContentTypeAndUser(restaurant.getId(), ContentType.RESTAURANT, user);

            RandomRestaurantRes randomRestaurantRes = RandomRestaurantRes.builder()
                    .restaurantId(restaurant.getId())
                    .thumbnail(restaurant.getThumbnail())
                    .name(restaurant.getName())
                    .isBookmark(findBookmark.isPresent())
                    .build();
            restaurantResList.add(randomRestaurantRes);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(restaurantResList)
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    // home - 권한 동의 o, 10km 이내 랜덤 식당 3개 조회
    public ResponseEntity<?> findRandomRestaurantWithPermission(UserPrincipal userPrincipal, Long count, LocationReq locationReq) {

        // 지구의 반지름
        final int EARTH_RADIUS = 6371;

        User user = userService.validateUserById(userPrincipal.getId());
        List<Restaurant> restaurants = restaurantRepository.findAll();

        double userLatitude = Double.parseDouble(locationReq.getLatitude());
        double userLongitude = Double.parseDouble(locationReq.getLongitude());

        List<RandomRestaurantRes> restaurantResList = new ArrayList<>(); // 근처 식당 모음
        List<RandomRestaurantRes> randomRestaurantResList = new ArrayList<>(); // 랜덤 3개 응답

        if(!restaurants.isEmpty()) {
            for (Restaurant restaurant : restaurants) {
                double restaurantLatitude = Double.parseDouble(restaurant.getLatitude());
                double restaurantLongitude = Double.parseDouble(restaurant.getLongitude());

                double distance = calculateDistance(userLatitude, userLongitude, restaurantLatitude, restaurantLongitude);

                Optional<Bookmark> findBookmark = bookmarkRepository.findByContentIdAndContentTypeAndUser(restaurant.getId(), ContentType.RESTAURANT, user);

                // 10km 안에 있는 식당들만 포함
                if (distance <= 10) {
                    RandomRestaurantRes randomRestaurantRes = RandomRestaurantRes.builder()
                            .restaurantId(restaurant.getId())
                            .name(restaurant.getName())
                            .thumbnail(restaurant.getThumbnail())
                            .isBookmark(findBookmark.isPresent())
                            .build();
                    restaurantResList.add(randomRestaurantRes);
                }
            }
        }
        // 랜덤 수 count개(3개) 추리기
        Set<Integer> randomNum = new HashSet<>();
        while(randomNum.size() < count){
            randomNum.add((int)(Math.random() * restaurantResList.size()));
        }

        Iterator<Integer> iter = randomNum.iterator();
        while(iter.hasNext()){
            int num = iter.next();
            randomRestaurantResList.add(restaurantResList.get(num));
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(randomRestaurantResList)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // Map 1depth - 식당 리스트 조회 : 가까운 순
    public ResponseEntity<?> findAroundRestaurantList(LocationReq locationReq, Integer page) {

        // 식당 id, 식당 이름, 식당 카테고리(한식, 양식 등), 내 위치로부터의 거리 (m 단위), 별점, 썸네일 이미지
        Pageable pageable = PageRequest.of(page, 10);

        double userLatitude = Double.parseDouble(locationReq.getLatitude());
        double userLongitude = Double.parseDouble(locationReq.getLongitude());

        Page<Restaurant> restaurantPage = restaurantRepository.findRestaurantsNearUser(userLatitude, userLongitude, pageable);
        List<Restaurant> restaurantList = restaurantPage.getContent();
        List<RestaurantBannerRes> restaurantBannerResList = new ArrayList<>();

        for (Restaurant restaurant : restaurantList) {
            double distance = calculateDistance(userLatitude, userLongitude,  Double.parseDouble(restaurant.getLatitude()),  Double.parseDouble(restaurant.getLongitude()));

            RestaurantBannerRes restaurantBannerRes = RestaurantBannerRes.builder()
                    .restaurantId(restaurant.getId())
                    .restaurantName(restaurant.getName())
                    .restaurantType(restaurant.getRestaurantType())
                    .distance(distance)
                    .rate(restaurant.getRate())
                    .thumbnail(restaurant.getThumbnail())
                    .build();
            restaurantBannerResList.add(restaurantBannerRes);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(restaurantBannerResList)
                .build();

        return ResponseEntity.ok(apiResponse);

    }


    // Description : 유저 - 식당 거리 계산 함수
    private double calculateDistance(double userLatitude, double userLongitude, double restaurantLatitude, double restaurantLongitude) {
        double dLatitude = Math.toRadians(restaurantLatitude - userLatitude);
        double dLongitude = Math.toRadians(restaurantLongitude - userLongitude);

        double a = Math.sin(dLatitude / 2) * Math.sin(dLatitude / 2)
                + Math.cos(Math.toRadians(userLatitude)) * Math.cos(Math.toRadians(restaurantLatitude))
                * Math.sin(dLongitude / 2) * Math.sin(dLongitude / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 두 지점 간의 직선 거리를 반환 (단위: km)
        return EARTH_RADIUS * c; // distance
    }
}

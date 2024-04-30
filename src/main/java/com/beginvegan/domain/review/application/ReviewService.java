package com.beginvegan.domain.review.application;

import com.beginvegan.domain.image.domain.Image;
import com.beginvegan.domain.image.domain.repository.ImageRepository;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.restaurant.domain.repository.RestaurantRepository;
import com.beginvegan.domain.restaurant.dto.response.RestaurantDetailRes;
import com.beginvegan.domain.restaurant.exception.InvalidRestaurantException;
import com.beginvegan.domain.review.domain.Review;
import com.beginvegan.domain.review.domain.ReviewType;
import com.beginvegan.domain.review.domain.repository.ReviewRepository;
import com.beginvegan.domain.review.dto.PostReviewReq;
import com.beginvegan.domain.review.dto.RestaurantInfoRes;
import com.beginvegan.domain.review.dto.ReviewDetailRes;
import com.beginvegan.domain.review.dto.ReviewListRes;
import com.beginvegan.domain.s3.application.S3Uploader;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.domain.user.dto.UserDetailRes;
import com.beginvegan.domain.user.exception.InvalidUserException;
import com.beginvegan.global.DefaultAssert;
import com.beginvegan.global.config.security.token.UserPrincipal;
import com.beginvegan.global.payload.ApiResponse;
import com.beginvegan.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ImageRepository imageRepository;

    private final UserService userService;
    private final S3Uploader s3Uploader;

    // 식당 정보 조회
    public ResponseEntity<?> getRestaurantInfoForReview(UserPrincipal userPrincipal, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(InvalidUserException::new);

        RestaurantInfoRes restaurantInfoRes = RestaurantInfoRes.builder()
                .name(restaurant.getName())
                .restaurantType(restaurant.getRestaurantType())
                .address(restaurant.getAddress()).build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(restaurantInfoRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    //- 리뷰 작성
    @Transactional
    public ResponseEntity<?> postReview(UserPrincipal userPrincipal, PostReviewReq postReviewReq, MultipartFile[] images) {
        User user = userService.validateUserById(userPrincipal.getId());
        Restaurant restaurant = validateRestaurantById(postReviewReq.getRestaurantId());

        boolean hasImages = images != null && images.length > 0;
        ReviewType reviewType = hasImages ? ReviewType.PHOTO : ReviewType.NORMAL;

        Review review = Review.builder()
                .content(postReviewReq.getContent())
                .reviewType(reviewType)
                .rate(postReviewReq.getRate())
                .user(user)
                .restaurant(restaurant)
                .build();
        reviewRepository.save(review);

        if (hasImages) { uploadReviewImages(images, review); }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("리뷰가 등록되었습니다.").build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private void uploadReviewImages(MultipartFile[] images, Review review) {
        List<Image> reviewImages = new ArrayList<>();
        for (MultipartFile reviewImage : images) {
            String imageUrl = s3Uploader.uploadImage(reviewImage);
            Image image = Image.builder()
                    .review(review)
                    .imageUrl(imageUrl)
                    .build();
            reviewImages.add(image);
        }
        imageRepository.saveAll(reviewImages);
    }

    // 매일 0시 정각 리뷰 평점 업데이트
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateReviewRatings() {
        LocalDate today = LocalDate.now();
        // 변경된 리뷰가 있는 식당 가져오기
        List<Restaurant> updatedRestaurants = reviewRepository.findDistinctRestaurantsByModifiedDate(today);
        for (Restaurant restaurant : updatedRestaurants) {
            // 평균 평점 구하기
            BigDecimal averageRate = reviewRepository.findAverageRateByRestaurant(restaurant);
            if (averageRate != null) {
                // 소수점 둘째 자리에서 반올림
                BigDecimal roundedAverageRate = averageRate.setScale(1, RoundingMode.HALF_UP);
                restaurant.updateRate(roundedAverageRate.doubleValue());
            }
        }
    }

    //- 리뷰 목록 조회
    //- 리뷰 추천
    //- 포토 리뷰 필터
    //- 리뷰 수정
    //- 리뷰 삭제
    //- 검증된 리뷰 삭제시 리워드 회수

    public ResponseEntity<?> findReviewsByUser(UserPrincipal userPrincipal, Integer page) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);

        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "date"));
        Page<Review> reviewPage = reviewRepository.findReviewsByUser(user, pageRequest);

        List<Review> reviews = reviewPage.getContent();
        List<ReviewDetailRes> reviewDetailResList = reviews.stream()
                .map(review -> ReviewDetailRes.builder()
                        .id(review.getId())
                        .content(review.getContent())
                        .date(review.getCreatedDate().toLocalDate())
                        .restaurantDetailRes(RestaurantDetailRes.toDto(review.getRestaurant()))
                        .userDetailRes(UserDetailRes.toDto(user))
                        .build())
                .toList();

        ReviewListRes reviewListRes = ReviewListRes.builder()
                .reviews(reviewDetailResList)
                .totalCount(reviewPage.getTotalElements())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(reviewListRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    public Restaurant validateRestaurantById(Long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        DefaultAssert.isTrue(restaurant.isPresent(), "식당 정보가 올바르지 않습니다.");
        return restaurant.get();
    }

}

package com.beginvegan.domain.review.application;

import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.restaurant.domain.repository.RestaurantRepository;
import com.beginvegan.domain.restaurant.dto.response.RestaurantDetailRes;
import com.beginvegan.domain.restaurant.exception.InvalidRestaurantException;
import com.beginvegan.domain.review.domain.Review;
import com.beginvegan.domain.review.domain.repository.ReviewRepository;
import com.beginvegan.domain.review.dto.PostReviewReq;
import com.beginvegan.domain.review.dto.RestaurantInfoRes;
import com.beginvegan.domain.review.dto.ReviewDetailRes;
import com.beginvegan.domain.review.dto.ReviewListRes;
import com.beginvegan.domain.s3.application.S3Uploader;
import com.beginvegan.domain.user.domain.User;
import com.beginvegan.domain.user.domain.repository.UserRepository;
import com.beginvegan.domain.user.dto.UserDetailRes;
import com.beginvegan.domain.user.exception.InvalidUserException;
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

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Uploader s3Uploader;

    // 식당 정보 조회
    public ResponseEntity<?> getRestaurantInfoForReview(UserPrincipal userPrincipal, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(userPrincipal.getId())
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
    //- 이미지 저장
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
                        .date(review.getDate())
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

    @Transactional
    public ResponseEntity<?> postReview(UserPrincipal userPrincipal, PostReviewReq postReviewReq) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(InvalidUserException::new);
        Restaurant restaurant = restaurantRepository.findById(postReviewReq.getRestaurantId())
                .orElseThrow(InvalidRestaurantException::new);

        Review review = Review.builder()
                .content(postReviewReq.getContent())
                .date(LocalDate.now())
                .restaurant(restaurant)
                .user(user)
                .build();

        reviewRepository.save(review);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("리뷰가 등록되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}

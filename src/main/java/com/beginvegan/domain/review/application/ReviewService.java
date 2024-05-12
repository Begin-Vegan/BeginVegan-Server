package com.beginvegan.domain.review.application;

import com.beginvegan.domain.common.Status;
import com.beginvegan.domain.image.domain.Image;
import com.beginvegan.domain.image.domain.repository.ImageRepository;
import com.beginvegan.domain.recommendation.domain.Recommendation;
import com.beginvegan.domain.recommendation.domain.repository.RecommendationRepository;
import com.beginvegan.domain.report.domain.Report;
import com.beginvegan.domain.report.domain.repository.ReportRepository;
import com.beginvegan.domain.report.dto.ReportContentReq;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.restaurant.domain.repository.RestaurantRepository;
import com.beginvegan.domain.review.domain.Review;
import com.beginvegan.domain.review.domain.ReviewType;
import com.beginvegan.domain.review.domain.repository.ReviewRepository;
import com.beginvegan.domain.review.dto.request.PostReviewReq;
import com.beginvegan.domain.review.dto.request.UpdateReviewReq;
import com.beginvegan.domain.review.dto.response.MyReviewRes;
import com.beginvegan.domain.review.dto.response.RecommendationByUserAndReviewRes;
import com.beginvegan.domain.review.dto.response.RestaurantInfoRes;
import com.beginvegan.domain.review.dto.response.ReviewDetailRes;
import com.beginvegan.domain.s3.application.S3Uploader;
import com.beginvegan.domain.suggestion.domain.parent.Inspection;
import com.beginvegan.domain.user.application.UserService;
import com.beginvegan.domain.user.domain.User;
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
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReportRepository reportRepository;
    private final RestaurantRepository restaurantRepository;
    private final ImageRepository imageRepository;
    private final RecommendationRepository recommendationRepository;

    private final UserService userService;
    private final S3Uploader s3Uploader;

    // 리뷰 작성 시 식당 정보 조회
    public ResponseEntity<?> getRestaurantInfoForReview(UserPrincipal userPrincipal, Long restaurantId) {
        Restaurant restaurant = validateRestaurantById(restaurantId);

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

    public ResponseEntity<?> getReviewInfo(UserPrincipal userPrincipal, Long reviewId) {
        Review review = validateReviewById(reviewId);
        List<Image> images = imageRepository.findByReview(review);

        RestaurantInfoRes restaurantInfoRes = RestaurantInfoRes.builder()
                .name(review.getRestaurant().getName())
                .address(review.getRestaurant().getAddress())
                .restaurantType(review.getRestaurant().getRestaurantType())
                .build();

        ReviewDetailRes reviewDetailRes = ReviewDetailRes.builder()
                .id(reviewId)
                .content(review.getContent())
                .rate(review.getRate())
                .restaurantInfoRes(restaurantInfoRes)
                .images(images)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(reviewDetailRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 리뷰 등록
    @Transactional
    public ResponseEntity<?> postReview(UserPrincipal userPrincipal, PostReviewReq postReviewReq, Optional<MultipartFile[]> images) {
        User user = userService.validateUserById(userPrincipal.getId());
        Restaurant restaurant = validateRestaurantById(postReviewReq.getRestaurantId());

        boolean hasImages = images.isPresent();
        ReviewType reviewType = hasImages ? ReviewType.PHOTO : ReviewType.NORMAL;

        Review review = Review.builder()
                .content(postReviewReq.getContent())
                .reviewType(reviewType)
                .rate(postReviewReq.getRate())
                .user(user)
                .restaurant(restaurant)
                .build();
        reviewRepository.save(review);

        if (hasImages) { uploadReviewImages(images.get(), review); }

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
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        // 변경된 리뷰가 있는 식당 가져오기
        List<Restaurant> updatedRestaurants = reviewRepository.findDistinctRestaurantsByModifiedDate(start, end);
        for (Restaurant restaurant : updatedRestaurants) {
            // 평균 평점 구하기
            BigDecimal averageRate = reviewRepository.findAverageRateByRestaurant(restaurant);
            // 소수점 둘째 자리에서 반올림
            BigDecimal roundedAverageRate = averageRate.setScale(1, RoundingMode.HALF_UP);
            restaurant.updateRate(roundedAverageRate.doubleValue());
        }
    }

    // 리뷰 추천
    @Transactional
    public ResponseEntity<?> recommendReviews(UserPrincipal userPrincipal, Long reviewId) {
        User user = userService.validateUserById(userPrincipal.getId());
        Review review = validateReviewById(reviewId);

        boolean isRecommend = true;
        // 리뷰 추천(포인트 부여) - 취소 - 재추천 시 포인트 부여하지 않음
        // recommendation 테이블 soft delete
        if (recommendationRepository.existsByUserAndReview(user, review)) {
            Recommendation recommendation = recommendationRepository.findByUserAndReview(user, review);
            if (recommendation.getStatus() == Status.DELETE) {          // 재추천
                recommendation.updateStatus(Status.ACTIVE);
            } else {      // 추천 취소
                recommendation.updateStatus(Status.DELETE);
                isRecommend = false;
            }
        } else {
            Recommendation recommendation = Recommendation.builder()
                    .review(review)
                    .user(user).build();
            recommendationRepository.save(recommendation);
            // 리뷰 작성자에게 포인트 부여
            review.getUser().updatePoint(2);
        }

        int count = recommendationRepository.countByReviewAndStatus(review, Status.ACTIVE);

        RecommendationByUserAndReviewRes recommendationRes = RecommendationByUserAndReviewRes.builder()
                .recommendationCount(count)
                .isRecommendation(isRecommend)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(recommendationRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 리뷰 수정
    // Description : 수정 시 무조건 이미지 삭제(사용자가 이미지를 삭제했는지, 변경사항이 없는지 구분이 불가함. 따라서 리뷰 수정 시 이미지는 무조건 삭제 후 다시 저장하는 방향으로)
    @Transactional
    public ResponseEntity<?> updateReview(UserPrincipal userPrincipal, Long reviewId, UpdateReviewReq updateReviewReq, Optional<MultipartFile[]> images) {
        User user = userService.validateUserById(userPrincipal.getId());
        Review review = validateReviewById(reviewId);

        DefaultAssert.isTrue(review.getUser() == user, "리뷰 수정 권한이 없습니다.");
        review.updateReview(updateReviewReq.getContent(), updateReviewReq.getRate());
        // 수정 시 무조건 이미지 삭제
        deleteReviewImages(review);
        // 이미지 존재하면 업로드
        if (images.isPresent()) {
            uploadReviewImages(images.get(), review);
            // 이미지 여부에 따라 리뷰 타입 변경
            review.updateReviewType(ReviewType.PHOTO);
            // 검증 필요하므로 초기화
            review.updateInspection(Inspection.INCOMPLETE);
        } else {
            // 검증된 리뷰 수정 시 사진 삭제하면 포인트 차감
            if (review.getReviewType() == ReviewType.PHOTO && review.getInspection() == Inspection.COMPLETE_REWARD) { user.subPoint(3);}
            review.updateReviewType(ReviewType.NORMAL);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("리뷰가 수정되었습니다.").build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 리뷰 삭제 - 검증된 리뷰 삭제시 리워드 회수
    @Transactional
    public ResponseEntity<?> deleteReview(UserPrincipal userPrincipal, Long reviewId) {
            User user = userService.validateUserById(userPrincipal.getId());
            Review review = validateReviewById(reviewId);

            DefaultAssert.isTrue(review.getUser() == user, "리뷰 삭제 권한이 없습니다.");
            if (review.getInspection() == Inspection.COMPLETE_REWARD) {
                user.subPoint(3);
            }
            // 이미지 삭제
            deleteReviewImages(review);
            reviewRepository.delete(review);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("리뷰가 삭제되었습니다.").build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private void deleteReviewImages(Review review) {
        if (!imageRepository.findByReview(review).isEmpty()) {
            List<Image> originalImages = imageRepository.findByReview(review);
            // s3에서 삭제
            for (Image image : originalImages) {
                String originalFile = image.getImageUrl().split("amazonaws.com/")[1];
                s3Uploader.deleteFile(originalFile);
            }
            imageRepository.deleteAll(originalImages);
        }
    }

    // 리뷰 신고
    @Transactional
    public ResponseEntity<?> reportReview(UserPrincipal userPrincipal, Long reviewId, ReportContentReq reportContentReq) {
        User user = userService.validateUserById(userPrincipal.getId());
        Review review = validateReviewById(reviewId);

        // 추후 필요 시 enum 값으로 수정
        Report report = Report.builder()
                .user(user)
                .review(review)
                .content(reportContentReq.getContent())
                .build();
        reportRepository.save(report);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("신고가 접수되었습니다.").build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }


    public ResponseEntity<?> findReviewsByUser(UserPrincipal userPrincipal, Integer page) {
        User user = userService.validateUserById(userPrincipal.getId());
        PageRequest pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "modifiedDate"));

        Page<Review> myReviews = reviewRepository.findReviewsByUserAndVisible(user, pageable, true);
        List<MyReviewRes> myReviewResList = myReviews.stream()
                .map(review -> MyReviewRes.builder()
                        .reviewId(review.getId())
                        .restaurantName(review.getRestaurant().getName())
                        .date(review.getModifiedDate().toLocalDate())
                        .rate(review.getRate())
                        .content(review.getContent())
                        .countRecommendation(recommendationRepository.countByReviewAndStatus(review, Status.ACTIVE))
                        .isRecommendation(recommendationRepository.existsByUserAndReviewAndStatus(user, review, Status.ACTIVE))
                        .images(imageRepository.findByReview(review))
                        .build())
                .toList();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(myReviewResList)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    public Restaurant validateRestaurantById(Long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        DefaultAssert.isTrue(restaurant.isPresent(), "식당 정보가 올바르지 않습니다.");
        return restaurant.get();
    }

    public Review validateReviewById(Long reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        DefaultAssert.isTrue(review.isPresent(), "리뷰 정보가 올바르지 않습니다.");
        return review.get();
    }

}

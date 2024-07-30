package com.beginvegan.domain.restaurant.domain.repository;

import com.beginvegan.domain.restaurant.domain.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @EntityGraph(attributePaths = {"menus"})
    Optional<Restaurant> findRestaurantById(Long id);

    @EntityGraph(attributePaths = {"menus"})
    @Query("SELECT r FROM Restaurant r")
    List<Restaurant> findAllWithMenus();

    @Query(value = "SELECT *, (" +
            "6371 * acos(" +
            "cos(radians(:userLatitude)) * cos(radians(latitude)) *" +
            "cos(radians(longitude) - radians(:userLongitude)) +" +
            "sin(radians(:userLatitude)) * sin(radians(latitude))" +
            ")" +
            ") AS distance " +
            "FROM restaurant " +
            "ORDER BY distance ASC",
            countQuery = "SELECT count(*) FROM restaurant",
            nativeQuery = true)
    Page<Restaurant> findRestaurantsNearUser(@Param("userLatitude") double userLatitude,
                                             @Param("userLongitude") double userLongitude,
                                             Pageable pageable);

    // 리뷰 수로 정렬 + 검색어 포함 페이징
    @Query("SELECT r FROM Restaurant r " +
            "WHERE " +
            "(:keyword IS NULL OR " +
            "    (LOWER(r.address.province) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(r.address.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(r.restaurantType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     EXISTS (SELECT m FROM Menu m WHERE m.restaurant = r AND LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))))) " +
            "ORDER BY " +
            "   (SELECT COUNT(rev) FROM Review rev WHERE rev.restaurant = r) DESC, " +
            "   CASE " +
            "       WHEN LOWER(r.address.province) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "            LOWER(r.address.city) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 1 " +
            "       WHEN LOWER(r.restaurantType) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 2 " +
            "       ELSE 3 " +
            "   END, " +
            "r.name ASC")
    Page<Restaurant> searchWithPriorityAndReviewOrder(@Param("keyword") String keyword, Pageable pageable);

    // 가까운 순 정렬 + 검색어 포함 페이징
    @Query(value = "SELECT r.* FROM restaurant r " +
            "WHERE " +
            "(:keyword IS NULL OR " +
            "    (LOWER(r.province) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(r.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(r.restaurant_type) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     EXISTS (SELECT m.id FROM menu m WHERE m.restaurant_id = r.id AND LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))))) " +
            "ORDER BY " +
            "   (6371 * acos(cos(radians(:userLatitude)) * cos(radians(r.latitude)) *" +
            "   cos(radians(r.longitude) - radians(:userLongitude)) + sin(radians(:userLatitude)) *" +
            "   sin(radians(r.latitude)))) ASC, " +
            "r.name ASC", nativeQuery = true)
    Page<Restaurant> searchWithPriorityAndDistanceNative(@Param("keyword") String keyword,
                                                         @Param("userLatitude") double userLatitude,
                                                         @Param("userLongitude") double userLongitude,
                                                         Pageable pageable);

    // 북마크 수로 정렬 + 검색어 포함 페이징
    @Query("SELECT r FROM Restaurant r " +
            "WHERE " +
            "(:keyword IS NULL OR " +
            "    (LOWER(r.address.province) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(r.address.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(r.restaurantType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     EXISTS (SELECT m FROM Menu m WHERE m.restaurant = r AND LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))))) " +
            "ORDER BY " +
            "   (SELECT COUNT(b) FROM Bookmark b WHERE b.contentId = r.id AND b.contentType = 'RESTAURANT') DESC, " +
            "   CASE " +
            "       WHEN LOWER(r.address.province) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "            LOWER(r.address.city) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 1 " +
            "       WHEN LOWER(r.restaurantType) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 2 " +
            "       ELSE 3 " +
            "   END, " +
            "r.name ASC")
    Page<Restaurant> searchWithPriorityAndBookmarkOrder(@Param("keyword") String keyword, Pageable pageable);
}

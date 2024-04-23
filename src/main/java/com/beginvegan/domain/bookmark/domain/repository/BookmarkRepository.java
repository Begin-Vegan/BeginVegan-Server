package com.beginvegan.domain.bookmark.domain.repository;

import com.beginvegan.domain.bookmark.domain.Bookmark;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

//    @EntityGraph(attributePaths = {"restaurant"})
//    Page<Bookmark> findBookmarksByUser(User user, Pageable pageable);

//    Bookmark findBookmarkByUserAndRestaurant(User user, Restaurant restaurant);

//    boolean existsBookmarkByUserAndRestaurant(User user, Restaurant restaurant);

    Optional<Bookmark> findByContentIdAndContentTypeAndUser(Long contentId, ContentType contentType, User user);

}

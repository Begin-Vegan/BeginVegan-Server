package com.beginvegan.domain.bookmark.domain.repository;

import com.beginvegan.domain.bookmark.domain.Bookmark;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByContentIdAndContentTypeAndUser(Long contentId, ContentType contentType, User user);

    boolean existsBookmarkByContentIdAndContentTypeAndUser(Long contentId, ContentType contentType, User user);
}

package com.beginvegan.domain.bookmark.domain;

import com.beginvegan.domain.bookmark.domain.repository.ContentType;
import com.beginvegan.domain.common.BaseEntity;
import com.beginvegan.domain.restaurant.domain.Restaurant;
import com.beginvegan.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Bookmark extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 매거진, 레시피, 레스토랑 id
    private Long contentId;

    // 매거진 / 레시피 / 레스토랑
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Builder
    public Bookmark(Long id, User user, Long contentId, ContentType contentType) {
        this.id = id;
        this.user = user;
        this.contentId = contentId;
        this.contentType = contentType;
    }
}

package com.beginvegan.domain.bookmark.dto.request;

import com.beginvegan.domain.bookmark.domain.repository.ContentType;
import lombok.Data;

@Data
public class BookmarkReq {

    private Long contentId; // 매거진 or 레시피 or 식당 id

    private ContentType contentType; // MAGAZINE, RECIPE, RESTAURANT

}

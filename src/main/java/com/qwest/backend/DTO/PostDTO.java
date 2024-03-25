package com.qwest.backend.DTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PostDTO {
    private Long id;
    private Long authorId;
    private String date;
    private String href;
    private String title;
    private String featuredImage;
    private String desc;
    private Integer commentCount;
    private Integer viewedCount;
    private Integer readingTime;
    private String postType; // Enum as String
    private List<Long> categoryIds; // IDs of categories
}

package com.qwest.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ReviewDTO {
    private Long id;
    private Long authorId;
    private Long stayListingId;
    private int rating;
    private String comment;
    private LocalDate createdAt;
    private String authorName;
    private String authorAvatar;
    private String stayTitle;
}
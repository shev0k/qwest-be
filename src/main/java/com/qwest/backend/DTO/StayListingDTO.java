package com.qwest.backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StayListingDTO {
    private Long id;
    private Long authorId;
    private String date;
    private String href;
    private String title;
    private String featuredImage;
    private List<String> galleryImgs;
    private Integer commentCount;
    private Integer viewCount;
    private String address;
    private Double reviewStart;
    private Integer reviewCount;
    private Boolean isLiked;
    private String price;
    private Long listingCategoryId;
    private Integer maxGuests;
    private Integer bedrooms;
    private Integer bathrooms;
    private String saleOff;
    private Boolean isAds;
    private Double lat;
    private Double lng;
}

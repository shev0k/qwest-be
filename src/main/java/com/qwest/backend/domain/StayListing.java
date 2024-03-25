package com.qwest.backend.domain;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class StayListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    private String date;
    private String href;
    private String title;
    private String featuredImage;

    @ElementCollection
    private List<String> galleryImgs;

    private Integer commentCount;
    private Integer viewCount;
    private String address;
    private Double reviewStart;
    private Integer reviewCount;
    private Boolean isLiked;
    private String price;

    @ManyToOne
    @JoinColumn(name = "listing_category_id")
    private Taxonomy listingCategory;

    private Integer maxGuests;
    private Integer bedrooms;
    private Integer bathrooms;
    private String saleOff;
    private Boolean isAds;

    private Double lat;
    private Double lng;
}

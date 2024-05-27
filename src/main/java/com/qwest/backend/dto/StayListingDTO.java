package com.qwest.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class StayListingDTO {
    private Long id;
    private Long authorId;
    private LocalDate date;
    private String title;
    private String featuredImage;
    private List<String> galleryImageUrls; // URLs of images
    private Double reviewStart;
    private Integer reviewCount;

    // Location Information
    private String country;
    private String street;
    private String roomNumber; // Optional
    private String city;
    private String state;
    private String postalCode;

    // Enums represented as Strings for simplicity
    private String propertyType;
    private String rentalFormType;

    // Property Specifications
    private Double acreage;
    private Integer maxGuests;
    private Integer bedrooms;
    private Integer beds;
    private Integer bathrooms;
    private Integer kitchens;

    // Check-in / Check-out hours and special restrictions
    private String checkInHours;
    private String checkOutHours;

    private List<String> specialRestrictions;

    // Accommodation Description
    private String accommodationDescription;

    // Property Rates
    private Double weekdayPrice;
    private Double weekendPrice;
    private Double longTermStayDiscount;

    // Stay Duration
    private Integer minimumNights;
    private Integer maximumNights;

    // Location Coordinates
    private Double lat;
    private Double lng;

    // IDs of selected amenities for this listing
    private Set<Long> amenityIds;
    private Set<String> amenityNames;

    private List<LocalDate> availableDates;

    private Set<Long> likedByAuthorIds;
}

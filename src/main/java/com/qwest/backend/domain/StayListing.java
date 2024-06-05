package com.qwest.backend.domain;

import com.qwest.backend.domain.util.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private LocalDate date;
    private String title;
    private String featuredImage;

    @OneToMany(mappedBy = "stayListing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GalleryImage> galleryImages;

    private Double reviewStart;
    private Integer reviewCount;

    // Location Information
    private String country;
    private String street;
    private String roomNumber;
    private String city;
    private String state;
    private String postalCode;

    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    private RentalFormType rentalFormType;

    // Property Specifications
    private Double acreage;
    private Integer maxGuests;
    private Integer bedrooms;
    private Integer beds;
    private Integer bathrooms;
    private Integer kitchens;

    // Check-in / Check-out hours
    private String checkInHours;
    private String checkOutHours;

    @ElementCollection
    private List<String> specialRestrictions;

    // Accommodation Description
    @Lob
    @Column(name = "accommodation_description", columnDefinition = "TEXT")
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

    @ManyToMany
    @JoinTable(
            name = "stay_listing_amenities",
            joinColumns = @JoinColumn(name = "stay_listing_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private Set<Amenity> amenities;

    @OneToMany(mappedBy = "stayListing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingCalendar> bookingCalendar = new ArrayList<>();

    @ManyToMany(mappedBy = "wishlist")
    private Set<Author> likedByAuthors = new HashSet<>();
}

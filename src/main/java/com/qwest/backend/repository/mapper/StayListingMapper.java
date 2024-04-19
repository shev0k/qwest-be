package com.qwest.backend.repository.mapper;

import com.qwest.backend.dto.StayListingDTO;
import com.qwest.backend.domain.StayListing;
import com.qwest.backend.domain.Amenity;
import com.qwest.backend.domain.util.BookingCalendar;
import com.qwest.backend.domain.util.GalleryImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StayListingMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "amenityIds", source = "amenities", qualifiedByName = "amenitiesToIds")
    @Mapping(target = "amenityNames", source = "amenities", qualifiedByName = "amenitiesToNames")
    @Mapping(target = "galleryImageUrls", source = "galleryImages", qualifiedByName = "galleryImagesToUrls")
    @Mapping(target = "availableDates", source = "bookingCalendar", qualifiedByName = "bookingCalendarToAvailableDates")
    StayListingDTO toDto(StayListing stayListing);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "galleryImages", ignore = true)
    @Mapping(target = "bookingCalendar", ignore = true)
    StayListing toEntity(StayListingDTO dto);

    @Named("amenitiesToIds")
    static Set<Long> amenitiesToIds(Set<Amenity> amenities) {
        return amenities != null ? amenities.stream()
                .map(Amenity::getId)
                .collect(Collectors.toSet()) : null;
    }

    @Named("galleryImagesToUrls")
    static List<String> galleryImagesToUrls(List<GalleryImage> galleryImages) {
        return galleryImages != null ? galleryImages.stream()
                .map(GalleryImage::getImageUrl)
                .toList() : null;
    }

    @Named("bookingCalendarToAvailableDates")
    static List<LocalDate> bookingCalendarToAvailableDates(List<BookingCalendar> bookingCalendar) {
        return bookingCalendar != null ? bookingCalendar.stream()
                .filter(BookingCalendar::getIsAvailable)
                .map(BookingCalendar::getDate)
                .toList() : null;
    }

    @Named("amenitiesToNames")
    static Set<String> amenitiesToNames(Set<Amenity> amenities) {
        return amenities.stream()
                .map(Amenity::getName)
                .collect(Collectors.toSet());
    }
}

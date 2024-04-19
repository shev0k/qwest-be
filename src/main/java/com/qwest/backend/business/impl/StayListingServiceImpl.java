package com.qwest.backend.business.impl;

import com.qwest.backend.domain.StayListing;
import com.qwest.backend.dto.StayListingDTO;
import com.qwest.backend.domain.geocoding.GeocodingService;
import com.qwest.backend.domain.geocoding.LatLng;
import com.qwest.backend.domain.Amenity;
import com.qwest.backend.domain.util.BookingCalendar;
import com.qwest.backend.domain.util.GalleryImage;
import com.qwest.backend.repository.mapper.StayListingMapper;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.StayListingRepository;
import com.qwest.backend.repository.AmenityRepository;
import com.qwest.backend.business.StayListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StayListingServiceImpl implements StayListingService {

    private final StayListingRepository stayListingRepository;
    private final AuthorRepository authorRepository;
    private final AmenityRepository amenityRepository;
    private final StayListingMapper stayListingMapper;
    private final GeocodingService geocodingService;

    @Autowired
    public StayListingServiceImpl(StayListingRepository stayListingRepository,
                                  AuthorRepository authorRepository,
                                  AmenityRepository amenityRepository,
                                  StayListingMapper stayListingMapper,
                                  GeocodingService geocodingService) {
        this.stayListingRepository = stayListingRepository;
        this.authorRepository = authorRepository;
        this.amenityRepository = amenityRepository;
        this.stayListingMapper = stayListingMapper;
        this.geocodingService = geocodingService;
    }

    @Override
    public List<StayListingDTO> findAllDto() {
        return stayListingRepository.findAll()
                .stream()
                .map(stayListingMapper::toDto)
                .toList();
    }

    @Override
    public Optional<StayListingDTO> findById(Long id) {
        return stayListingRepository.findById(id)
                .map(stayListingMapper::toDto);
    }

    @Override
    @Transactional
    public StayListingDTO save(StayListingDTO stayListingDTO) {
        StayListing stayListing = stayListingMapper.toEntity(stayListingDTO);

        // Set the author
        authorRepository.findById(stayListingDTO.getAuthorId()).ifPresent(stayListing::setAuthor);

        // Fetch geolocation
        String fullAddress = String.format("%s, %s, %s, %s, %s", stayListing.getStreet(), stayListing.getCity(), stayListing.getState(), stayListing.getPostalCode(), stayListing.getCountry());
        LatLng latLng = geocodingService.getLatLngForAddress(fullAddress);
        stayListing.setLat(latLng.getLatitude());
        stayListing.setLng(latLng.getLongitude());

        // Handle amenities association
        if (stayListingDTO.getAmenityIds() != null && !stayListingDTO.getAmenityIds().isEmpty()) {
            Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(stayListingDTO.getAmenityIds()));
            stayListing.setAmenities(amenities);
        }

        // Gallery images handling
        List<GalleryImage> galleryImages = stayListingDTO.getGalleryImageUrls().stream()
                .map(url -> {
                    GalleryImage galleryImage = new GalleryImage();
                    galleryImage.setImageUrl(url);
                    galleryImage.setStayListing(stayListing);
                    return galleryImage;
                }).toList();
        stayListing.setGalleryImages(galleryImages);

        // Booking Calendar handling
        List<BookingCalendar> bookingCalendars = stayListingDTO.getAvailableDates().stream()
                .map(date -> {
                    BookingCalendar bookingCalendar = new BookingCalendar();
                    bookingCalendar.setDate(date);
                    bookingCalendar.setIsAvailable(true);
                    bookingCalendar.setStayListing(stayListing);
                    return bookingCalendar;
                }).toList();
        stayListing.setBookingCalendar(bookingCalendars);

        StayListing savedListing = stayListingRepository.save(stayListing);
        return stayListingMapper.toDto(savedListing);
    }

    @Override
    public void deleteById(Long id) {
        stayListingRepository.deleteById(id);
    }
}

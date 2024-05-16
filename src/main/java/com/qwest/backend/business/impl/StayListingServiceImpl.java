package com.qwest.backend.business.impl;

import com.qwest.backend.domain.StayListing;
import com.qwest.backend.dto.StayListingDTO;
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


    @Autowired
    public StayListingServiceImpl(StayListingRepository stayListingRepository,
                                  AuthorRepository authorRepository,
                                  AmenityRepository amenityRepository,
                                  StayListingMapper stayListingMapper) {
        this.stayListingRepository = stayListingRepository;
        this.authorRepository = authorRepository;
        this.amenityRepository = amenityRepository;
        this.stayListingMapper = stayListingMapper;
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

        stayListing.setLat(stayListingDTO.getLat());
        stayListing.setLng(stayListingDTO.getLng());

        // Handle amenities association
        if (stayListingDTO.getAmenityIds() != null && !stayListingDTO.getAmenityIds().isEmpty()) {
            Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(stayListingDTO.getAmenityIds()));
            stayListing.setAmenities(amenities);
        }

        // Gallery images handling
        List<GalleryImage> galleryImages = stayListingDTO.getGalleryImageUrls() != null ?
                stayListingDTO.getGalleryImageUrls().stream()
                        .map(url -> {
                            GalleryImage galleryImage = new GalleryImage();
                            galleryImage.setImageUrl(url);
                            galleryImage.setStayListing(stayListing);
                            return galleryImage;
                        }).toList() : null;
        stayListing.setGalleryImages(galleryImages);

        // Booking Calendar handling
        List<BookingCalendar> bookingCalendars = stayListingDTO.getAvailableDates() != null ?
                stayListingDTO.getAvailableDates().stream()
                        .map(date -> {
                            BookingCalendar bookingCalendar = new BookingCalendar();
                            bookingCalendar.setDate(date);
                            bookingCalendar.setIsAvailable(true);
                            bookingCalendar.setStayListing(stayListing);
                            return bookingCalendar;
                        }).toList() : null;
        stayListing.setBookingCalendar(bookingCalendars);

        StayListing savedListing = stayListingRepository.save(stayListing);
        return stayListingMapper.toDto(savedListing);
    }

    @Override
    public void deleteById(Long id) {
        stayListingRepository.deleteById(id);
    }
}

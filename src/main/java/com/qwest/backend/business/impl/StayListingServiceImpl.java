package com.qwest.backend.business.impl;

import com.qwest.backend.business.WebSocketNotificationService;
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
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StayListingServiceImpl implements StayListingService {

    private final StayListingRepository stayListingRepository;
    private final AuthorRepository authorRepository;
    private final AmenityRepository amenityRepository;
    private final StayListingMapper stayListingMapper;
    private final WebSocketNotificationService webSocketNotificationService;

    @Autowired
    public StayListingServiceImpl(StayListingRepository stayListingRepository,
                                  AuthorRepository authorRepository,
                                  AmenityRepository amenityRepository,
                                  StayListingMapper stayListingMapper,
                                  WebSocketNotificationService webSocketNotificationService) {
        this.stayListingRepository = stayListingRepository;
        this.authorRepository = authorRepository;
        this.amenityRepository = amenityRepository;
        this.stayListingMapper = stayListingMapper;
        this.webSocketNotificationService = webSocketNotificationService;
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
        StayListingDTO savedListingDTO = stayListingMapper.toDto(savedListing);

        webSocketNotificationService.broadcastChange("NEW_STAY_LISTING", savedListingDTO);

        return savedListingDTO;
    }

    @Override
    public void deleteById(Long id) {
        stayListingRepository.deleteById(id);
        webSocketNotificationService.broadcastChange("DELETED_STAY_LISTING", id);
    }

    @Override
    public List<StayListingDTO> findByFilters(String location, LocalDate startDate, LocalDate endDate, Integer guests,
                                              List<String> typeOfStay, Double priceMin, Double priceMax, Integer bedrooms,
                                              Integer beds, Integer bathrooms, List<String> propertyType, Pageable pageable) {
        return stayListingRepository.findByFilters(location, startDate, endDate, guests, typeOfStay, priceMin, priceMax,
                        bedrooms, beds, bathrooms, propertyType, pageable)
                .map(stayListingMapper::toDto)
                .getContent();
    }

    @Override
    @Transactional
    public StayListingDTO updateAvailableDates(Long stayListingId, List<LocalDate> dates) {
        System.out.println("Updating available dates for StayListing id: " + stayListingId);
        System.out.println("Input dates: " + dates);

        StayListing stayListing = stayListingRepository.findById(stayListingId)
                .orElseThrow(() -> new EntityNotFoundException("Stay listing not found with id " + stayListingId));

        List<BookingCalendar> bookingCalendar = stayListing.getBookingCalendar();
        System.out.println("Existing booking calendar: " + bookingCalendar);

        for (LocalDate date : dates) {
            boolean isDateAlreadyUnavailable = bookingCalendar.stream()
                    .anyMatch(bc -> bc.getDate().equals(date) && bc.getIsAvailable());

            if (!isDateAlreadyUnavailable) {
                // If the date is not already present as unavailable, add it as unavailable
                BookingCalendar booking = new BookingCalendar();
                booking.setDate(date);
                booking.setIsAvailable(true); // Mark the date as unavailable
                booking.setStayListing(stayListing);
                bookingCalendar.add(booking);
            }
        }

        stayListing.setBookingCalendar(bookingCalendar);

        StayListing updatedStayListing = stayListingRepository.save(stayListing);
        StayListingDTO updatedStayListingDTO = stayListingMapper.toDto(updatedStayListing);

        System.out.println("Updated booking calendar: " + updatedStayListing.getBookingCalendar());

        return updatedStayListingDTO;
    }

    @Override
    @Transactional
    public StayListingDTO removeUnavailableDates(Long stayListingId, LocalDate checkInDate, LocalDate checkOutDate) {
        StayListing stayListing = stayListingRepository.findById(stayListingId)
                .orElseThrow(() -> new EntityNotFoundException("Stay listing not found with id " + stayListingId));

        List<BookingCalendar> bookingCalendar = stayListing.getBookingCalendar();

        List<LocalDate> datesToMakeAvailable = checkInDate.datesUntil(checkOutDate).toList();

        bookingCalendar.removeIf(bc -> datesToMakeAvailable.contains(bc.getDate()) && bc.getIsAvailable());

        stayListing.setBookingCalendar(bookingCalendar);

        StayListing updatedStayListing = stayListingRepository.save(stayListing);
        return stayListingMapper.toDto(updatedStayListing);
    }



}
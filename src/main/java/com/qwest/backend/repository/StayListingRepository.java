package com.qwest.backend.repository;

import com.qwest.backend.domain.StayListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface StayListingRepository extends JpaRepository<StayListing, Long> {

    @Query("SELECT s FROM StayListing s WHERE (:location IS NULL OR s.country = :location) " +
            "AND (:startDate IS NULL OR :endDate IS NULL OR NOT EXISTS " +
            "(SELECT b FROM BookingCalendar b WHERE b.stayListing.id = s.id " +
            "AND ((b.date = :startDate) OR (b.date = :endDate) OR (b.date BETWEEN :startDate AND :endDate)) " +
            "AND b.isAvailable = false)) " +
            "AND (:guests IS NULL OR s.maxGuests >= :guests) " +
            "AND (:typeOfStay IS NULL OR s.rentalFormType IN :typeOfStay) " +
            "AND ((:priceMin IS NULL AND :priceMax IS NULL) OR " +
            "(s.weekdayPrice BETWEEN :priceMin AND :priceMax OR s.weekendPrice BETWEEN :priceMin AND :priceMax)) " +
            "AND (:bedrooms IS NULL OR s.bedrooms >= :bedrooms) " +
            "AND (:beds IS NULL OR s.beds >= :beds) " +
            "AND (:bathrooms IS NULL OR s.bathrooms >= :bathrooms) " +
            "AND (:propertyType IS NULL OR s.propertyType IN :propertyType)")
    Page<StayListing> findByFilters(@Param("location") String location,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    @Param("guests") Integer guests,
                                    @Param("typeOfStay") List<String> typeOfStay,
                                    @Param("priceMin") Double priceMin,
                                    @Param("priceMax") Double priceMax,
                                    @Param("bedrooms") Integer bedrooms,
                                    @Param("beds") Integer beds,
                                    @Param("bathrooms") Integer bathrooms,
                                    @Param("propertyType") List<String> propertyType,
                                    Pageable pageable);
}

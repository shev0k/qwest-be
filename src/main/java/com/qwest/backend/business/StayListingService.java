package com.qwest.backend.business;

import com.qwest.backend.dto.StayListingDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StayListingService {
    List<StayListingDTO> findAllDto();
    Optional<StayListingDTO> findById(Long id);
    StayListingDTO save(StayListingDTO stayListingDTO);
    void deleteById(Long id);
    List<StayListingDTO> findByFilters(String location, LocalDate startDate, LocalDate endDate, Integer guests,
                                       List<String> typeOfStay, Double priceMin, Double priceMax, Integer bedrooms,
                                       Integer beds, Integer bathrooms, List<String> propertyType, Pageable pageable);
}

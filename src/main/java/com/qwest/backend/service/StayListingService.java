package com.qwest.backend.service;

import com.qwest.backend.DTO.StayListingDTO;
import java.util.List;
import java.util.Optional;

public interface StayListingService {
    List<StayListingDTO> findAllDto();
    Optional<StayListingDTO> findById(Long id);
    StayListingDTO save(StayListingDTO stayListingDTO);
    void deleteById(Long id);
}

package com.qwest.backend.business;

import com.qwest.backend.dto.StayListingDTO;
import java.util.List;
import java.util.Optional;

public interface StayListingService {
    List<StayListingDTO> findAllDto();
    Optional<StayListingDTO> findById(Long id);
    StayListingDTO save(StayListingDTO stayListingDTO);
    void deleteById(Long id);
}

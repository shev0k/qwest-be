package com.qwest.backend.business;

import com.qwest.backend.dto.AmenityDTO;

import java.util.List;

public interface AmenityService {
    AmenityDTO createAmenity(AmenityDTO dto);
    void deleteAmenity(Long amenityId);
    List<AmenityDTO> getAllAmenities();
}
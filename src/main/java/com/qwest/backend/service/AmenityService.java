package com.qwest.backend.service;

import com.qwest.backend.DTO.AmenityDTO;

import java.util.List;

public interface AmenityService {
    AmenityDTO createAmenity(AmenityDTO dto);
    void deleteAmenity(Long amenityId);
    List<AmenityDTO> getAllAmenities();
}
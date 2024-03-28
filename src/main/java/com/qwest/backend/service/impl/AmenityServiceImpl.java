package com.qwest.backend.service.impl;

import com.qwest.backend.DTO.AmenityDTO;
import com.qwest.backend.domain.util.Amenity;
import com.qwest.backend.mapper.AmenityMapper;
import com.qwest.backend.repository.AmenityRepository;
import com.qwest.backend.service.AmenityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmenityServiceImpl implements AmenityService {
    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    @Autowired
    public AmenityServiceImpl(AmenityRepository amenityRepository, AmenityMapper amenityMapper) {
        this.amenityRepository = amenityRepository;
        this.amenityMapper = amenityMapper;
    }

    @Override
    public AmenityDTO createAmenity(AmenityDTO dto) {
        Amenity amenity = amenityMapper.toEntity(dto);
        return amenityMapper.toDto(amenityRepository.save(amenity));
    }

    @Override
    public void deleteAmenity(Long amenityId) {
        amenityRepository.deleteById(amenityId);
    }

    @Override
    public List<AmenityDTO> getAllAmenities() {
        return amenityRepository.findAll().stream()
                .map(amenityMapper::toDto)
                .collect(Collectors.toList());
    }
}

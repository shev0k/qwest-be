package com.qwest.backend.business;

import com.qwest.backend.dto.AmenityDTO;
import com.qwest.backend.domain.Amenity;
import com.qwest.backend.repository.mapper.AmenityMapper;
import com.qwest.backend.repository.AmenityRepository;
import com.qwest.backend.business.impl.AmenityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTest {

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private AmenityMapper amenityMapper;

    @InjectMocks
    private AmenityServiceImpl amenityService;

    private AmenityDTO amenityDTO;
    private Amenity amenity;

    @BeforeEach
    void setUp() {
        amenity = new Amenity();

        amenityDTO = new AmenityDTO();
    }

    @Test
    void createAmenityTest() {
        when(amenityMapper.toEntity(any(AmenityDTO.class))).thenReturn(amenity);
        when(amenityRepository.save(any(Amenity.class))).thenReturn(amenity);
        when(amenityMapper.toDto(any(Amenity.class))).thenReturn(amenityDTO);

        AmenityDTO savedDTO = amenityService.createAmenity(amenityDTO);

        assertNotNull(savedDTO);
        verify(amenityRepository).save(any(Amenity.class));
        verify(amenityMapper).toDto(any(Amenity.class));
    }

    @Test
    void deleteAmenityTest() {
        doNothing().when(amenityRepository).deleteById(anyLong());

        amenityService.deleteAmenity(1L);

        verify(amenityRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllAmenitiesTest() {
        when(amenityRepository.findAll()).thenReturn(Collections.singletonList(amenity));
        when(amenityMapper.toDto(any(Amenity.class))).thenReturn(amenityDTO);

        List<AmenityDTO> results = amenityService.getAllAmenities();

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        verify(amenityRepository).findAll();
        verify(amenityMapper).toDto(any(Amenity.class));
    }

}

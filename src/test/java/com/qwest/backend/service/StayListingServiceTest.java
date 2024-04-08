package com.qwest.backend.service;

import com.qwest.backend.DTO.StayListingDTO;
import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.StayListing;
import com.qwest.backend.domain.geocoding.GeocodingService;
import com.qwest.backend.domain.geocoding.LatLng;
import com.qwest.backend.domain.util.Amenity;
import com.qwest.backend.mapper.StayListingMapper;
import com.qwest.backend.repository.AmenityRepository;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.StayListingRepository;
import com.qwest.backend.service.impl.StayListingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StayListingServiceTest {

    @Mock
    private StayListingRepository stayListingRepository;

    @Mock
    private StayListingMapper stayListingMapper;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private GeocodingService geocodingService;

    @InjectMocks
    private StayListingServiceImpl stayListingService;

    private StayListingDTO stayListingDTO;
    private StayListing stayListing;

    @BeforeEach
    void setUp() {
        stayListing = new StayListing();
        stayListing.setId(1L);
        stayListing.setTitle("Test Stay");

        stayListingDTO = new StayListingDTO();
        stayListingDTO.setId(1L);
        stayListingDTO.setTitle("Test Stay");
        stayListingDTO.setAmenityIds(new HashSet<>(List.of(1L)));
    }

    @Test
    void findAllDtoTest() {
        when(stayListingRepository.findAll()).thenReturn(Collections.singletonList(stayListing));
        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        var results = stayListingService.findAllDto();

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(stayListingDTO.getTitle(), results.get(0).getTitle());
    }

    @Test
    void findByIdTest() {
        when(stayListingRepository.findById(1L)).thenReturn(Optional.of(stayListing));
        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        var result = stayListingService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(stayListingDTO.getTitle(), result.get().getTitle());
    }

    @Test
    void saveStayListingTest() {
        Author mockAuthor = new Author();
        mockAuthor.setId(stayListingDTO.getAuthorId());
        when(authorRepository.findById(stayListingDTO.getAuthorId())).thenReturn(Optional.of(mockAuthor));

        LatLng mockLatLng = new LatLng(1.0, 1.0);
        when(geocodingService.getLatLngForAddress(anyString())).thenReturn(mockLatLng);

        Amenity mockAmenity = new Amenity();
        mockAmenity.setId(1L);
        when(amenityRepository.findAllById(stayListingDTO.getAmenityIds()))
                .thenReturn(new ArrayList<>(Collections.singletonList(mockAmenity)));

        when(stayListingMapper.toEntity(any(StayListingDTO.class))).thenReturn(stayListing);
        when(stayListingRepository.save(any(StayListing.class))).thenReturn(stayListing);

        stayListingDTO.setGalleryImageUrls(Arrays.asList(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg"));
        stayListingDTO.setAvailableDates(Collections.singletonList(LocalDate.now()));

        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        StayListingDTO saved = stayListingService.save(stayListingDTO);

        assertNotNull(saved);
        assertEquals(stayListingDTO.getTitle(), saved.getTitle());

        verify(authorRepository).findById(stayListingDTO.getAuthorId());
        verify(geocodingService).getLatLngForAddress(anyString());
        verify(amenityRepository).findAllById(stayListingDTO.getAmenityIds());
        verify(stayListingRepository).save(any(StayListing.class));
        verify(stayListingMapper).toDto(any(StayListing.class));
    }

    @Test
    void saveStayListingTest_AmenityIdsNull() {
        stayListingDTO.setAmenityIds(null);
        stayListingDTO.setGalleryImageUrls(Arrays.asList("https://example.com/image1.jpg", "https://example.com/image2.jpg"));
        stayListingDTO.setAvailableDates(Collections.singletonList(LocalDate.now()));

        Author mockAuthor = new Author();
        mockAuthor.setId(stayListingDTO.getAuthorId());
        when(authorRepository.findById(stayListingDTO.getAuthorId())).thenReturn(Optional.of(mockAuthor));

        LatLng mockLatLng = new LatLng(1.0, 1.0);
        when(geocodingService.getLatLngForAddress(anyString())).thenReturn(mockLatLng);

        when(stayListingMapper.toEntity(any(StayListingDTO.class))).thenReturn(stayListing);
        when(stayListingRepository.save(any(StayListing.class))).thenReturn(stayListing);
        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        StayListingDTO saved = stayListingService.save(stayListingDTO);

        assertNotNull(saved);
        assertEquals(stayListingDTO.getTitle(), saved.getTitle());
        verify(amenityRepository, never()).findAllById(any());
        verify(stayListingRepository).save(stayListing);
    }

    @Test
    void saveStayListingTest_AmenityIdsEmpty() {
        stayListingDTO.setAmenityIds(Collections.emptySet());
        stayListingDTO.setGalleryImageUrls(Arrays.asList("https://example.com/image1.jpg", "https://example.com/image2.jpg"));
        stayListingDTO.setAvailableDates(Collections.singletonList(LocalDate.now()));

        Author mockAuthor = new Author();
        mockAuthor.setId(stayListingDTO.getAuthorId());
        when(authorRepository.findById(stayListingDTO.getAuthorId())).thenReturn(Optional.of(mockAuthor));

        LatLng mockLatLng = new LatLng(1.0, 1.0);
        when(geocodingService.getLatLngForAddress(anyString())).thenReturn(mockLatLng);

        when(stayListingMapper.toEntity(any(StayListingDTO.class))).thenReturn(stayListing);
        when(stayListingRepository.save(any(StayListing.class))).thenReturn(stayListing);
        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        StayListingDTO saved = stayListingService.save(stayListingDTO);

        assertNotNull(saved);
        assertEquals(stayListingDTO.getTitle(), saved.getTitle());
        verify(amenityRepository, never()).findAllById(any()); // Ensuring that no call is made to findAllById
        verify(stayListingRepository).save(stayListing);
    }



    @Test
    void deleteByIdTest() {
        doNothing().when(stayListingRepository).deleteById(1L);

        stayListingService.deleteById(1L);

        verify(stayListingRepository, times(1)).deleteById(1L);
    }
}

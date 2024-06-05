package com.qwest.backend.business;

import com.qwest.backend.business.impl.StayListingServiceImpl;
import com.qwest.backend.domain.Amenity;
import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.StayListing;
import com.qwest.backend.domain.util.BookingCalendar;
import com.qwest.backend.dto.StayListingDTO;
import com.qwest.backend.repository.AmenityRepository;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.StayListingRepository;
import com.qwest.backend.repository.mapper.StayListingMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private WebSocketNotificationService webSocketNotificationService;

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
        stayListingDTO.setAuthorId(1L);
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
        verify(amenityRepository).findAllById(stayListingDTO.getAmenityIds());
        verify(stayListingRepository).save(any(StayListing.class));
        verify(stayListingMapper).toDto(any(StayListing.class));
        verify(webSocketNotificationService).broadcastChange(eq("NEW_STAY_LISTING"), any(StayListingDTO.class));
    }

    @Test
    void saveStayListingTest_AmenityIdsNull() {
        stayListingDTO.setAmenityIds(null);
        stayListingDTO.setGalleryImageUrls(Arrays.asList("https://example.com/image1.jpg", "https://example.com/image2.jpg"));
        stayListingDTO.setAvailableDates(Collections.singletonList(LocalDate.now()));

        Author mockAuthor = new Author();
        mockAuthor.setId(stayListingDTO.getAuthorId());
        when(authorRepository.findById(stayListingDTO.getAuthorId())).thenReturn(Optional.of(mockAuthor));

        when(stayListingMapper.toEntity(any(StayListingDTO.class))).thenReturn(stayListing);
        when(stayListingRepository.save(any(StayListing.class))).thenReturn(stayListing);
        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        StayListingDTO saved = stayListingService.save(stayListingDTO);

        assertNotNull(saved);
        assertEquals(stayListingDTO.getTitle(), saved.getTitle());
        verify(amenityRepository, never()).findAllById(any());
        verify(stayListingRepository).save(stayListing);
        verify(webSocketNotificationService).broadcastChange(eq("NEW_STAY_LISTING"), any(StayListingDTO.class));
    }

    @Test
    void saveStayListingTest_AmenityIdsEmpty() {
        stayListingDTO.setAmenityIds(Collections.emptySet());
        stayListingDTO.setGalleryImageUrls(Arrays.asList("https://example.com/image1.jpg", "https://example.com/image2.jpg"));
        stayListingDTO.setAvailableDates(Collections.singletonList(LocalDate.now()));

        Author mockAuthor = new Author();
        mockAuthor.setId(stayListingDTO.getAuthorId());
        when(authorRepository.findById(stayListingDTO.getAuthorId())).thenReturn(Optional.of(mockAuthor));

        when(stayListingMapper.toEntity(any(StayListingDTO.class))).thenReturn(stayListing);
        when(stayListingRepository.save(any(StayListing.class))).thenReturn(stayListing);
        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        StayListingDTO saved = stayListingService.save(stayListingDTO);

        assertNotNull(saved);
        assertEquals(stayListingDTO.getTitle(), saved.getTitle());
        verify(amenityRepository, never()).findAllById(any());
        verify(stayListingRepository).save(stayListing);
        verify(webSocketNotificationService).broadcastChange(eq("NEW_STAY_LISTING"), any(StayListingDTO.class));
    }

    @Test
    void saveStayListingTest_GalleryImagesNull() {
        stayListingDTO.setGalleryImageUrls(null);
        stayListingDTO.setAvailableDates(Collections.singletonList(LocalDate.now()));

        when(stayListingMapper.toEntity(any(StayListingDTO.class))).thenReturn(stayListing);
        when(stayListingRepository.save(any(StayListing.class))).thenReturn(stayListing);
        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        StayListingDTO saved = stayListingService.save(stayListingDTO);

        assertNotNull(saved);
        assertEquals(stayListingDTO.getTitle(), saved.getTitle());
        assertNull(saved.getGalleryImageUrls());
        verify(stayListingRepository).save(stayListing);
        verify(webSocketNotificationService).broadcastChange(eq("NEW_STAY_LISTING"), any(StayListingDTO.class));
    }

    @Test
    void saveStayListingTest_AvailableDatesNull() {
        stayListingDTO.setGalleryImageUrls(Arrays.asList("https://example.com/image1.jpg", "https://example.com/image2.jpg"));
        stayListingDTO.setAvailableDates(null);

        when(stayListingMapper.toEntity(any(StayListingDTO.class))).thenReturn(stayListing);
        when(stayListingRepository.save(any(StayListing.class))).thenReturn(stayListing);
        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        StayListingDTO saved = stayListingService.save(stayListingDTO);

        assertNotNull(saved);
        assertEquals(stayListingDTO.getTitle(), saved.getTitle());
        assertNull(saved.getAvailableDates());
        verify(stayListingRepository).save(stayListing);
        verify(webSocketNotificationService).broadcastChange(eq("NEW_STAY_LISTING"), any(StayListingDTO.class));
    }

    @Test
    void deleteByIdTest() {
        doNothing().when(stayListingRepository).deleteById(1L);

        stayListingService.deleteById(1L);

        verify(stayListingRepository, times(1)).deleteById(1L);
        verify(webSocketNotificationService).broadcastChange(eq("DELETED_STAY_LISTING"), eq(1L));
    }

    @Test
    void updateAvailableDatesTest() {
        Long stayListingId = 1L;
        List<LocalDate> newDates = Arrays.asList(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        when(stayListingRepository.findById(stayListingId)).thenReturn(Optional.of(stayListing));
        when(stayListingRepository.save(any(StayListing.class))).thenReturn(stayListing);
        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        StayListingDTO result = stayListingService.updateAvailableDates(stayListingId, newDates);

        assertNotNull(result);
        assertEquals(stayListingDTO.getTitle(), result.getTitle());
        verify(stayListingRepository).findById(stayListingId);
        verify(stayListingRepository).save(any(StayListing.class));
    }

    @Test
    void removeUnavailableDatesTest() {
        Long stayListingId = 1L;
        LocalDate checkInDate = LocalDate.now().plusDays(1);
        LocalDate checkOutDate = LocalDate.now().plusDays(3);

        when(stayListingRepository.findById(stayListingId)).thenReturn(Optional.of(stayListing));
        when(stayListingRepository.save(any(StayListing.class))).thenReturn(stayListing);
        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        StayListingDTO result = stayListingService.removeUnavailableDates(stayListingId, checkInDate, checkOutDate);

        assertNotNull(result);
        assertEquals(stayListingDTO.getTitle(), result.getTitle());
        verify(stayListingRepository).findById(stayListingId);
        verify(stayListingRepository).save(any(StayListing.class));
    }

    @Test
    void findByFiltersTest() {
        List<StayListing> stayListings = Arrays.asList(stayListing);
        when(stayListingRepository.findByFilters(anyString(), any(), any(), any(), anyList(), anyDouble(), anyDouble(),
                anyInt(), anyInt(), anyInt(), anyList(), any())).thenReturn(new PageImpl<>(stayListings));
        when(stayListingMapper.toDto(any(StayListing.class))).thenReturn(stayListingDTO);

        var results = stayListingService.findByFilters("location", LocalDate.now(), LocalDate.now().plusDays(1), 2,
                Collections.singletonList("type"), 100.0, 200.0, 2, 2, 1, Collections.singletonList("property"), Pageable.unpaged());

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(stayListingDTO.getTitle(), results.get(0).getTitle());
    }
}

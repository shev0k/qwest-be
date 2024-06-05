package com.qwest.backend.business;

import com.qwest.backend.business.impl.ReviewServiceImpl;
import com.qwest.backend.domain.Review;
import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.StayListing;
import com.qwest.backend.dto.ReviewDTO;
import com.qwest.backend.repository.ReviewRepository;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.StayListingRepository;
import com.qwest.backend.repository.mapper.ReviewMapper;
import com.qwest.backend.business.WebSocketNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private StayListingRepository stayListingRepository;

    @Mock
    private WebSocketNotificationService webSocketNotificationService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private ReviewDTO reviewDTO;
    private Review review;
    private Author author;
    private StayListing stayListing;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);

        stayListing = new StayListing();
        stayListing.setId(1L);

        review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setComment("Excellent stay!");
        review.setAuthor(author);
        review.setStayListing(stayListing);

        reviewDTO = new ReviewDTO();
        reviewDTO.setId(1L);
        reviewDTO.setRating(5);
        reviewDTO.setComment("Excellent stay!");
        reviewDTO.setAuthorId(1L);
        reviewDTO.setStayListingId(1L);
    }

    @Test
    void saveReviewTest() {
        when(reviewMapper.toEntity(any(ReviewDTO.class))).thenReturn(review);
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(stayListingRepository.findById(anyLong())).thenReturn(Optional.of(stayListing));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewMapper.toDto(any(Review.class))).thenReturn(reviewDTO);

        ReviewDTO saved = reviewService.save(reviewDTO);

        assertNotNull(saved);
        assertEquals(reviewDTO.getComment(), saved.getComment());
        verify(authorRepository).findById(anyLong());
        verify(stayListingRepository).findById(anyLong());
        verify(reviewRepository).save(any(Review.class));
        verify(webSocketNotificationService).broadcastChange(eq("NEW_REVIEW"), any(ReviewDTO.class));
    }

    @Test
    void saveReviewWithInvalidAuthorIdTest() {
        when(reviewMapper.toEntity(any(ReviewDTO.class))).thenReturn(review);
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> reviewService.save(reviewDTO));
        assertEquals("Invalid author ID", exception.getMessage());
    }

    @Test
    void saveReviewWithInvalidStayListingIdTest() {
        when(reviewMapper.toEntity(any(ReviewDTO.class))).thenReturn(review);
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(stayListingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> reviewService.save(reviewDTO));
        assertEquals("Invalid stay listing ID", exception.getMessage());
    }

    @Test
    void updateReviewTest() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(stayListingRepository.findById(anyLong())).thenReturn(Optional.of(stayListing));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewMapper.toDto(any(Review.class))).thenReturn(reviewDTO);

        ReviewDTO updated = reviewService.update(1L, reviewDTO);

        assertNotNull(updated);
        assertEquals(reviewDTO.getComment(), updated.getComment());
        verify(reviewRepository).save(review);
        verify(webSocketNotificationService).broadcastChange(eq("UPDATED_REVIEW"), any(ReviewDTO.class));
    }

    @Test
    void updateNonExistingReviewTest() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> reviewService.update(1L, reviewDTO));
        assertEquals("Review not found", exception.getMessage());
    }

    @Test
    void deleteReviewTest() {
        doNothing().when(reviewRepository).deleteById(1L);

        reviewService.delete(1L);

        verify(reviewRepository, times(1)).deleteById(1L);
        verify(webSocketNotificationService).broadcastChange("DELETED_REVIEW", 1L);
    }

    @Test
    void getAllReviewsTest() {
        when(reviewRepository.findAll()).thenReturn(Collections.singletonList(review));
        when(reviewMapper.toDto(any(Review.class))).thenReturn(reviewDTO);

        List<ReviewDTO> reviews = reviewService.getAllReviews();

        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
        assertEquals(reviewDTO.getComment(), reviews.get(0).getComment());
    }

    @Test
    void getReviewsByStayListingTest() {
        when(reviewRepository.findByStayListingId(1L)).thenReturn(Collections.singletonList(review));
        when(reviewMapper.toDto(any(Review.class))).thenReturn(reviewDTO);

        List<ReviewDTO> reviews = reviewService.getReviewsByStayListing(1L);

        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
        assertEquals(reviewDTO.getComment(), reviews.get(0).getComment());
    }

    @Test
    void getReviewsByAuthorTest() {
        when(reviewRepository.findByStayListingAuthorId(1L)).thenReturn(Collections.singletonList(review));
        when(reviewMapper.toDto(any(Review.class))).thenReturn(reviewDTO);

        List<ReviewDTO> reviews = reviewService.getReviewsByAuthor(1L);

        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
        assertEquals(reviewDTO.getComment(), reviews.get(0).getComment());
    }

    @Test
    void getTotalReviewsTest() {
        when(reviewRepository.countByStayListingId(1L)).thenReturn(5L);

        long totalReviews = reviewService.getTotalReviews(1L);

        assertEquals(5L, totalReviews);
    }
}

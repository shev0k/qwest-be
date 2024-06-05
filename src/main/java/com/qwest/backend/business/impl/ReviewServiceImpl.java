package com.qwest.backend.business.impl;

import com.qwest.backend.business.WebSocketNotificationService;
import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.Review;
import com.qwest.backend.domain.StayListing;
import com.qwest.backend.dto.ReviewDTO;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.ReviewRepository;
import com.qwest.backend.repository.StayListingRepository;
import com.qwest.backend.repository.mapper.ReviewMapper;
import com.qwest.backend.business.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final AuthorRepository authorRepository;
    private final StayListingRepository stayListingRepository;
    private final WebSocketNotificationService webSocketNotificationService;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper,
                             AuthorRepository authorRepository, StayListingRepository stayListingRepository,
                             WebSocketNotificationService webSocketNotificationService) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.authorRepository = authorRepository;
        this.stayListingRepository = stayListingRepository;
        this.webSocketNotificationService = webSocketNotificationService;
    }

    @Override
    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(reviewMapper::toDto)
                .toList();
    }


    @Override
    public ReviewDTO save(ReviewDTO reviewDTO) {
        Review review = reviewMapper.toEntity(reviewDTO);
        Author author = authorRepository.findById(reviewDTO.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid author ID"));
        StayListing stayListing = stayListingRepository.findById(reviewDTO.getStayListingId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid stay listing ID"));
        review.setAuthor(author);
        review.setStayListing(stayListing);
        review.setCreatedAt(LocalDate.now());

        ReviewDTO savedReview = reviewMapper.toDto(reviewRepository.save(review));
        
        webSocketNotificationService.broadcastChange("NEW_REVIEW", savedReview);

        return savedReview;
    }


    @Override
    public ReviewDTO update(Long id, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        Author author = authorRepository.findById(reviewDTO.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid author ID"));
        StayListing stayListing = stayListingRepository.findById(reviewDTO.getStayListingId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid stay listing ID"));
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setAuthor(author);
        review.setStayListing(stayListing);
        review.setCreatedAt(LocalDate.now());

        ReviewDTO updatedReview = reviewMapper.toDto(reviewRepository.save(review));

        webSocketNotificationService.broadcastChange("UPDATED_REVIEW", updatedReview);

        return updatedReview;
    }

    @Override
    public void delete(Long id) {
        reviewRepository.deleteById(id);
        webSocketNotificationService.broadcastChange("DELETED_REVIEW", id);
    }

    @Override
    public List<ReviewDTO> getReviewsByStayListing(Long stayListingId) {
        return reviewRepository.findByStayListingId(stayListingId)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public List<ReviewDTO> getReviewsByAuthor(Long authorId) {
        return reviewRepository.findByStayListingAuthorId(authorId)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public List<ReviewDTO> getReviewsForAuthorStays(Long authorId) {
        return reviewRepository.findByStayListingAuthorId(authorId)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public long getTotalReviews(Long stayListingId) {
        return reviewRepository.countByStayListingId(stayListingId);
    }
}

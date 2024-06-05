package com.qwest.backend.business;

import com.qwest.backend.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {
    ReviewDTO save(ReviewDTO reviewDTO);
    ReviewDTO update(Long id, ReviewDTO reviewDTO);
    void delete(Long id);
    List<ReviewDTO> getReviewsByStayListing(Long stayListingId);
    List<ReviewDTO> getReviewsByAuthor(Long authorId);
    List<ReviewDTO> getReviewsForAuthorStays(Long authorId);
    long getTotalReviews(Long stayListingId);
    List<ReviewDTO> getAllReviews();
}
package com.qwest.backend.controller;

import com.qwest.backend.business.NotificationService;
import com.qwest.backend.business.ReviewService;
import com.qwest.backend.business.StayListingService;
import com.qwest.backend.dto.ReviewDTO;
import com.qwest.backend.dto.StayListingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final NotificationService notificationService;
    private final StayListingService stayListingService;

    @Autowired
    public ReviewController(ReviewService reviewService, NotificationService notificationService, StayListingService stayListingService) {
        this.reviewService = reviewService;
        this.notificationService = notificationService;
        this.stayListingService = stayListingService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TRAVELER', 'HOST', 'FOUNDER')")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        ReviewDTO savedReview = reviewService.save(reviewDTO);
        StayListingDTO stayListing = stayListingService.findById(savedReview.getStayListingId())
                .orElseThrow(() -> new RuntimeException("Stay listing not found"));

        notificationService.notifyStayReview(stayListing.getAuthorId(), savedReview.getAuthorId(), "");
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRAVELER', 'HOST', 'FOUNDER')")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.update(id, reviewDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRAVELER', 'HOST', 'FOUNDER')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stay/{stayListingId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByStayListing(@PathVariable Long stayListingId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByStayListing(stayListingId);
        long totalReviews = reviewService.getTotalReviews(stayListingId);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalReviews))
                .body(reviews);
    }

    @GetMapping("/author-stays/{authorId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsForAuthorStays(@PathVariable Long authorId) {
        List<ReviewDTO> reviews = reviewService.getReviewsForAuthorStays(authorId);
        return ResponseEntity.ok(reviews);
    }
}

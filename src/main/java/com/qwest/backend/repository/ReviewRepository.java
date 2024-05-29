package com.qwest.backend.repository;

import com.qwest.backend.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByStayListingId(Long stayListingId);
    List<Review> findByAuthorId(Long authorId);
    List<Review> findByStayListingAuthorId(Long authorId);
    long countByStayListingId(Long stayListingId);
}
package com.qwest.backend.repository;

import com.qwest.backend.domain.Review;
import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.StayListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void whenSave_thenReturnSavedReview() {
        Author author = new Author();
        author.setEmail("test@example.com");
        author = entityManager.persistAndFlush(author);

        StayListing stayListing = new StayListing();
        stayListing.setTitle("Lovely Cottage");
        stayListing = entityManager.persistAndFlush(stayListing);

        Review review = new Review();
        review.setAuthor(author);
        review.setStayListing(stayListing);
        review.setRating(5);
        review.setComment("Excellent stay!");

        Review savedReview = reviewRepository.save(review);

        assertNotNull(savedReview.getId());
        assertEquals("Excellent stay!", savedReview.getComment());
    }

    @Test
    void whenFindById_thenReturnReview() {
        Author author = new Author();
        author.setEmail("test@example.com");
        author = entityManager.persistAndFlush(author);

        StayListing stayListing = new StayListing();
        stayListing.setTitle("Lovely Cottage");
        stayListing = entityManager.persistAndFlush(stayListing);

        Review review = new Review();
        review.setAuthor(author);
        review.setStayListing(stayListing);
        review.setRating(5);
        review.setComment("Excellent stay!");
        review = entityManager.persistAndFlush(review);

        Optional<Review> foundReview = reviewRepository.findById(review.getId());

        assertTrue(foundReview.isPresent());
        assertEquals("Excellent stay!", foundReview.get().getComment());
    }

    @Test
    void whenDelete_thenRemoveReview() {
        Author author = new Author();
        author.setEmail("test@example.com");
        author = entityManager.persistAndFlush(author);

        StayListing stayListing = new StayListing();
        stayListing.setTitle("Lovely Cottage");
        stayListing = entityManager.persistAndFlush(stayListing);

        Review review = new Review();
        review.setAuthor(author);
        review.setStayListing(stayListing);
        review.setRating(5);
        review.setComment("Excellent stay!");
        review = entityManager.persistAndFlush(review);

        reviewRepository.delete(review);
        Optional<Review> deletedReview = reviewRepository.findById(review.getId());

        assertFalse(deletedReview.isPresent());
    }

    @Test
    void whenFindByStayListingId_thenReturnReviews() {
        Author author = new Author();
        author.setEmail("test@example.com");
        author = entityManager.persistAndFlush(author);

        StayListing stayListing = new StayListing();
        stayListing.setTitle("Lovely Cottage");
        stayListing = entityManager.persistAndFlush(stayListing);

        Review review = new Review();
        review.setAuthor(author);
        review.setStayListing(stayListing);
        review.setRating(5);
        review.setComment("Excellent stay!");
        entityManager.persistAndFlush(review);

        List<Review> reviews = reviewRepository.findByStayListingId(stayListing.getId());


        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
        assertEquals("Excellent stay!", reviews.get(0).getComment());
    }

    @Test
    void whenFindByAuthorId_thenReturnReviews() {
        Author author = new Author();
        author.setEmail("test@example.com");
        author = entityManager.persistAndFlush(author);

        StayListing stayListing = new StayListing();
        stayListing.setTitle("Lovely Cottage");
        stayListing = entityManager.persistAndFlush(stayListing);

        Review review = new Review();
        review.setAuthor(author);
        review.setStayListing(stayListing);
        review.setRating(5);
        review.setComment("Excellent stay!");
        entityManager.persistAndFlush(review);

        List<Review> reviews = reviewRepository.findByAuthorId(author.getId());

        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
        assertEquals("Excellent stay!", reviews.get(0).getComment());
    }
}

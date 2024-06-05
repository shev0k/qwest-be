package com.qwest.backend.controller;

import com.qwest.backend.configuration.security.token.JwtUtil;
import com.qwest.backend.dto.ReviewDTO;
import com.qwest.backend.configuration.security.SecurityConfig;
import com.qwest.backend.business.ReviewService;
import com.qwest.backend.business.NotificationService;
import com.qwest.backend.business.StayListingService;
import com.qwest.backend.dto.StayListingDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@Import({SecurityConfig.class, JwtUtil.class})
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private StayListingService stayListingService;

    @Test
    @WithMockUser(username = "admin", roles = {"TRAVELER"})
    void createReviewTest() throws Exception {
        ReviewDTO newReview = new ReviewDTO();
        newReview.setComment("Great place!");
        newReview.setStayListingId(1L);
        newReview.setAuthorId(2L);

        ReviewDTO savedReview = new ReviewDTO();
        savedReview.setId(1L);
        savedReview.setComment("Great place!");
        savedReview.setStayListingId(1L);
        savedReview.setAuthorId(2L);

        StayListingDTO stayListing = new StayListingDTO();
        stayListing.setId(1L);
        stayListing.setAuthorId(3L);

        when(reviewService.save(any(ReviewDTO.class))).thenReturn(savedReview);
        when(stayListingService.findById(anyLong())).thenReturn(Optional.of(stayListing));

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"Great place!\", \"stayListingId\":1, \"authorId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.comment", is("Great place!")));

        verify(notificationService).notifyStayReview(eq(3L), eq(2L), eq("left a review on your stay"), eq(1L));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"TRAVELER"})
    void updateReviewTest() throws Exception {
        Long reviewId = 1L;
        ReviewDTO updatedReview = new ReviewDTO();
        updatedReview.setId(reviewId);
        updatedReview.setComment("Updated review");

        when(reviewService.update(anyLong(), any(ReviewDTO.class))).thenReturn(updatedReview);

        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"Updated review\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.comment", is("Updated review")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"TRAVELER"})
    void deleteReviewTest() throws Exception {
        doNothing().when(reviewService).delete(anyLong());

        mockMvc.perform(delete("/api/reviews/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"TRAVELER"})
    void getReviewsByStayListingTest() throws Exception {
        ReviewDTO review = new ReviewDTO();
        review.setId(1L);
        review.setComment("Awesome place");

        when(reviewService.getReviewsByStayListing(anyLong())).thenReturn(List.of(review));
        when(reviewService.getTotalReviews(anyLong())).thenReturn(1L);

        mockMvc.perform(get("/api/reviews/stay/{stayListingId}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].comment", is("Awesome place")))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"TRAVELER"})
    void getReviewsByAuthorTest() throws Exception {
        ReviewDTO review = new ReviewDTO();
        review.setId(1L);
        review.setComment("Great host");

        when(reviewService.getReviewsByAuthor(anyLong())).thenReturn(List.of(review));

        mockMvc.perform(get("/api/reviews/author/{authorId}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].comment", is("Great host")));
    }

}

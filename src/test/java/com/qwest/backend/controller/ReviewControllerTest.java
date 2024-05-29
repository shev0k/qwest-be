package com.qwest.backend.controller;

import com.qwest.backend.configuration.security.token.JwtUtil;
import com.qwest.backend.dto.ReviewDTO;
import com.qwest.backend.configuration.security.SecurityConfig;
import com.qwest.backend.business.ReviewService;
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

    @Test
    @WithMockUser(username="admin", roles={"TRAVELER"})
    void createReviewTest() throws Exception {
        ReviewDTO newReview = new ReviewDTO();
        newReview.setComment("Great place!");

        ReviewDTO savedReview = new ReviewDTO();
        savedReview.setId(1L);
        savedReview.setComment("Great place!");

        when(reviewService.save(any(ReviewDTO.class))).thenReturn(savedReview);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"Great place!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.comment", is("Great place!")));
    }

    @Test
    @WithMockUser(username="admin", roles={"TRAVELER"})
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
    @WithMockUser(username="admin", roles={"TRAVELER"})
    void deleteReviewTest() throws Exception {
        doNothing().when(reviewService).delete(anyLong());

        mockMvc.perform(delete("/api/reviews/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).delete(1L);
    }

    @Test
    void getReviewsByStayListingTest() throws Exception {
        ReviewDTO review = new ReviewDTO();
        review.setId(1L);
        review.setComment("Awesome place");

        when(reviewService.getReviewsByStayListing(anyLong(), any())).thenReturn(List.of(review));

        mockMvc.perform(get("/api/reviews/stay/{stayListingId}", 1)
                        .param("page", "0")
                        .param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].comment", is("Awesome place")));
    }

    @Test
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

package com.qwest.backend.controller;

import com.qwest.backend.configuration.security.token.JwtUtil;
import com.qwest.backend.dto.StayListingDTO;
import com.qwest.backend.configuration.security.SecurityConfig;
import com.qwest.backend.business.StayListingService;
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

@WebMvcTest(StayListingController.class)
@Import({SecurityConfig.class, JwtUtil.class})

class StayListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StayListingService stayListingService;

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void getAllStayListingsTest() throws Exception {
        StayListingDTO stayListing = new StayListingDTO();
        stayListing.setId(1L);
        stayListing.setTitle("Luxury Villa");

        when(stayListingService.findAllDto()).thenReturn(List.of(stayListing));

        mockMvc.perform(get("/api/stay-listings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Luxury Villa")));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void getStayListingByIdTest() throws Exception {
        StayListingDTO stayListing = new StayListingDTO();
        stayListing.setId(1L);
        stayListing.setTitle("Luxury Villa");

        when(stayListingService.findById(1L)).thenReturn(Optional.of(stayListing));

        mockMvc.perform(get("/api/stay-listings/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Luxury Villa")));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void createStayListingTest() throws Exception {
        StayListingDTO newListing = new StayListingDTO();
        newListing.setTitle("Beach House");

        StayListingDTO savedListing = new StayListingDTO();
        savedListing.setId(1L);
        savedListing.setTitle("Beach House");

        when(stayListingService.save(any(StayListingDTO.class))).thenReturn(savedListing);

        mockMvc.perform(post("/api/stay-listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Beach House\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Beach House")));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void updateStayListingTest() throws Exception {
        Long listingId = 1L;
        StayListingDTO updatedListing = new StayListingDTO();
        updatedListing.setId(listingId);
        updatedListing.setTitle("Updated Beach House");

        when(stayListingService.findById(listingId)).thenReturn(Optional.of(new StayListingDTO()));
        when(stayListingService.save(any(StayListingDTO.class))).thenReturn(updatedListing);

        mockMvc.perform(put("/api/stay-listings/{id}", listingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Beach House\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Beach House")));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void updateStayListing_NotFound() throws Exception {
        Long nonExistentListingId = 99L;

        when(stayListingService.findById(nonExistentListingId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/stay-listings/{id}", nonExistentListingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nonexistent Listing\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void deleteStayListing_Success() throws Exception {
        Long listingId = 1L;

        when(stayListingService.findById(listingId)).thenReturn(Optional.of(new StayListingDTO()));

        mockMvc.perform(delete("/api/stay-listings/{id}", listingId))
                .andExpect(status().isNoContent());

        verify(stayListingService).deleteById(listingId);
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void deleteStayListing_NotFound() throws Exception {
        Long nonExistentListingId = 99L;

        when(stayListingService.findById(nonExistentListingId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/stay-listings/{id}", nonExistentListingId))
                .andExpect(status().isNotFound());
    }
}

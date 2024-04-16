package com.qwest.backend.controller;

import com.qwest.backend.DTO.AmenityDTO;
import com.qwest.backend.configuration.SecurityConfig;
import com.qwest.backend.service.AmenityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AmenityController.class)
@Import(SecurityConfig.class)

class AmenityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AmenityService amenityService;

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void getAllAmenitiesTest() throws Exception {
        AmenityDTO amenity1 = new AmenityDTO();
        amenity1.setId(1L);
        amenity1.setName("Wi-Fi");

        when(amenityService.getAllAmenities()).thenReturn(List.of(amenity1));

        mockMvc.perform(get("/api/amenities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Wi-Fi")));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void createAmenityTest() throws Exception {
        AmenityDTO newAmenity = new AmenityDTO();
        newAmenity.setName("Pool");

        AmenityDTO savedAmenity = new AmenityDTO();
        savedAmenity.setId(1L);
        savedAmenity.setName("Pool");

        when(amenityService.createAmenity(any(AmenityDTO.class))).thenReturn(savedAmenity);

        mockMvc.perform(post("/api/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pool\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Pool")));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void deleteAmenityTest() throws Exception {
        Long amenityId = 1L;

        doNothing().when(amenityService).deleteAmenity(amenityId);

        mockMvc.perform(delete("/api/amenities/{id}", amenityId))
                .andExpect(status().isNoContent());

        verify(amenityService).deleteAmenity(amenityId);
    }
}

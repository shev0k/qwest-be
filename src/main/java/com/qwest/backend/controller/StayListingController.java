package com.qwest.backend.controller;

import com.qwest.backend.dto.StayListingDTO;
import com.qwest.backend.business.StayListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stay-listings")
public class StayListingController {

    private final StayListingService stayListingService;

    @Autowired
    public StayListingController(StayListingService stayListingService) {
        this.stayListingService = stayListingService;
    }

    @GetMapping
    public ResponseEntity<List<StayListingDTO>> getAllStayListings() {
        List<StayListingDTO> stayListings = stayListingService.findAllDto();
        return ResponseEntity.ok(stayListings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StayListingDTO> getStayListingById(@PathVariable Long id) {
        return stayListingService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StayListingDTO> createStayListing(@Valid @RequestBody StayListingDTO stayListingDTO) {
        StayListingDTO savedListing = stayListingService.save(stayListingDTO);
        return new ResponseEntity<>(savedListing, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StayListingDTO> updateStayListing(@PathVariable Long id, @Valid @RequestBody StayListingDTO stayListingDTO) {
        if (stayListingService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        stayListingDTO.setId(id); // Ensure the DTO has the correct ID
        StayListingDTO updatedListing = stayListingService.save(stayListingDTO);
        return ResponseEntity.ok(updatedListing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStayListing(@PathVariable Long id) {
        if (stayListingService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        stayListingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

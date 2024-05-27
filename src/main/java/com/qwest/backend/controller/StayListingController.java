package com.qwest.backend.controller;

import com.qwest.backend.dto.StayListingDTO;
import com.qwest.backend.business.StayListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

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
    public ResponseEntity<List<StayListingDTO>> getAllStayListings(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String dates,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) List<String> typeOfStay,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) Integer beds,
            @RequestParam(required = false) Integer bathrooms,
            @RequestParam(required = false) List<String> propertyType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        LocalDate startDate = null;
        LocalDate endDate = null;

        if (dates != null && !dates.isEmpty()) {
            try {
                String[] dateRange = dates.split(" - ");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                startDate = LocalDate.parse(dateRange[0], formatter);
                if (dateRange.length > 1) {
                    endDate = LocalDate.parse(dateRange[1], formatter);
                } else {
                    endDate = startDate;
                }
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        Pageable pageable = PageRequest.of(page, size);
        List<StayListingDTO> stayListings = stayListingService.findByFilters(location, startDate, endDate, guests,
                typeOfStay, priceMin, priceMax, bedrooms, beds, bathrooms, propertyType, pageable);
        return ResponseEntity.ok(stayListings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StayListingDTO> getStayListingById(@PathVariable Long id) {
        return stayListingService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST')")
    public ResponseEntity<StayListingDTO> createStayListing(@Valid @RequestBody StayListingDTO stayListingDTO) {
        StayListingDTO savedListing = stayListingService.save(stayListingDTO);
        return new ResponseEntity<>(savedListing, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST')")
    public ResponseEntity<StayListingDTO> updateStayListing(@PathVariable Long id, @Valid @RequestBody StayListingDTO stayListingDTO) {
        if (stayListingService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        stayListingDTO.setId(id);
        StayListingDTO updatedListing = stayListingService.save(stayListingDTO);
        return ResponseEntity.ok(updatedListing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST')")
    public ResponseEntity<Void> deleteStayListing(@PathVariable Long id) {
        if (stayListingService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        stayListingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

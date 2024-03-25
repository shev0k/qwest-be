package com.qwest.backend.controller;

import com.qwest.backend.DTO.ExperienceListingDTO;
import com.qwest.backend.service.ExperienceListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
public class ExperienceListingController {

    private final ExperienceListingService experienceListingService;

    @Autowired
    public ExperienceListingController(ExperienceListingService experienceListingService) {
        this.experienceListingService = experienceListingService;
    }

    @GetMapping
    public ResponseEntity<List<ExperienceListingDTO>> getAllExperiences() {
        List<ExperienceListingDTO> listings = experienceListingService.findAll();
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExperienceListingDTO> getExperienceById(@PathVariable Long id) {
        return experienceListingService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExperienceListingDTO> createExperience(@RequestBody ExperienceListingDTO dto) {
        ExperienceListingDTO savedDto = experienceListingService.save(dto);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExperienceListingDTO> updateExperience(@PathVariable Long id, @RequestBody ExperienceListingDTO dto) {
        if (!experienceListingService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        dto.setId(id);
        ExperienceListingDTO updatedDto = experienceListingService.save(dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
        if (!experienceListingService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        experienceListingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

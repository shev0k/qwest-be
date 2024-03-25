package com.qwest.backend.controller;

import com.qwest.backend.DTO.TaxonomyDTO;
import com.qwest.backend.service.TaxonomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxonomies")
public class TaxonomyController {

    private final TaxonomyService taxonomyService;

    @Autowired
    public TaxonomyController(TaxonomyService taxonomyService) {
        this.taxonomyService = taxonomyService;
    }

    @GetMapping
    public ResponseEntity<List<TaxonomyDTO>> getAllTaxonomies() {
        List<TaxonomyDTO> taxonomies = taxonomyService.findAllDto();
        return ResponseEntity.ok(taxonomies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxonomyDTO> getTaxonomyById(@PathVariable Long id) {
        return taxonomyService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TaxonomyDTO> createTaxonomy(@RequestBody TaxonomyDTO taxonomyDTO) {
        TaxonomyDTO savedTaxonomy = taxonomyService.save(taxonomyDTO);
        return ResponseEntity.ok(savedTaxonomy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxonomyDTO> updateTaxonomy(@PathVariable Long id, @RequestBody TaxonomyDTO taxonomyDTO) {
        if (!taxonomyService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        taxonomyDTO.setId(id); // Ensure the DTO has the correct ID
        TaxonomyDTO updatedTaxonomy = taxonomyService.save(taxonomyDTO);
        return ResponseEntity.ok(updatedTaxonomy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaxonomy(@PathVariable Long id) {
        if (!taxonomyService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        taxonomyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

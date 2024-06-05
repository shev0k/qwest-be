package com.qwest.backend.controller;

import com.qwest.backend.business.NotificationService;
import com.qwest.backend.dto.AuthorDTO;
import com.qwest.backend.business.AuthorService;
import com.qwest.backend.dto.PasswordResetDTO;
import com.qwest.backend.dto.StayListingDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.prepost.PreAuthorize;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/authors")
@Validated
public class AuthorController {

    private final AuthorService authorService;
    private final NotificationService notificationService;

    @Autowired
    public AuthorController(AuthorService authorService, NotificationService notificationService) {
        this.authorService = authorService;
        this.notificationService = notificationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        return authorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO authorDTO) {
        return new ResponseEntity<>(authorService.save(authorDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDTO authorDTO) {
        try {
            authorDTO.setId(id);
            AuthorDTO updatedAuthor = authorService.update(id, authorDTO);
            return ResponseEntity.ok(updatedAuthor);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping(value = "/{id}/avatar", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    public ResponseEntity<AuthorDTO> updateAuthorAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File upload request must contain a file.");
        }

        try {
            AuthorDTO updatedAuthor = authorService.updateAvatar(id, file);
            return ResponseEntity.ok(updatedAuthor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        if (authorService.findById(id).isPresent()) {
            authorService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthorDTO> login(@RequestBody AuthorDTO authorDTO) {
        return authorService.login(authorDTO)
                .map(dto -> ResponseEntity.ok().header("Authorization", "Bearer " + dto.getJwt()).body(dto))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetDTO passwordResetDTO) {
        authorService.resetPassword(passwordResetDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{authorId}/wishlist/{stayId}")
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    public ResponseEntity<AuthorDTO> addStayToWishlist(@PathVariable Long authorId, @PathVariable Long stayId) {
        AuthorDTO updatedAuthor = authorService.addStayToWishlist(authorId, stayId);
        return ResponseEntity.ok(updatedAuthor);
    }

    @DeleteMapping("/{authorId}/wishlist/{stayId}")
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    public ResponseEntity<AuthorDTO> removeStayFromWishlist(@PathVariable Long authorId, @PathVariable Long stayId) {
        AuthorDTO updatedAuthor = authorService.removeStayFromWishlist(authorId, stayId);
        return ResponseEntity.ok(updatedAuthor);
    }

    @GetMapping("/{authorId}/wishlist")
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    public ResponseEntity<List<StayListingDTO>> getWishlistedStays(@PathVariable Long authorId) {
        List<StayListingDTO> wishlistedStays = authorService.getWishlistedStays(authorId);
        return ResponseEntity.ok(wishlistedStays);
    }

    @GetMapping("/{authorId}/stay-listings")
    public ResponseEntity<List<StayListingDTO>> getStayListingsByAuthorId(@PathVariable Long authorId) {
        List<StayListingDTO> stayListings = authorService.getStayListingsByAuthorId(authorId);
        return ResponseEntity.ok(stayListings);
    }

    @PostMapping("/{id}/request-host")
    public ResponseEntity<AuthorDTO> requestHostRole(@PathVariable Long id) {
        AuthorDTO authorDTO = authorService.requestHostRole(id);
        notificationService.notifyHostRequest(id);
        return ResponseEntity.ok(authorDTO);
    }

    @PostMapping("/{id}/approve-host")
    @PreAuthorize("hasRole('FOUNDER')")
    public ResponseEntity<AuthorDTO> approveHostRole(@PathVariable Long id) {
        AuthorDTO authorDTO = authorService.approveHostRole(id);
        notificationService.notifyHostApproval(id);
        return ResponseEntity.ok(authorDTO);
    }

    @PostMapping("/{id}/reject-host")
    @PreAuthorize("hasRole('FOUNDER')")
    public ResponseEntity<AuthorDTO> rejectHostRole(@PathVariable Long id) {
        AuthorDTO authorDTO = authorService.rejectHostRole(id);
        notificationService.notifyHostRejection(id);
        return ResponseEntity.ok(authorDTO);
    }

    @PostMapping("/{id}/demote-traveler")
    @PreAuthorize("hasRole('FOUNDER')")
    public ResponseEntity<AuthorDTO> demoteToTraveler(@PathVariable Long id) {
        AuthorDTO authorDTO = authorService.demoteToTraveler(id);
        notificationService.notifyDemotionToTraveler(id);
        return ResponseEntity.ok(authorDTO);
    }
}

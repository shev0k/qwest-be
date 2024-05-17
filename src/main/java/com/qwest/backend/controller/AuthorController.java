package com.qwest.backend.controller;

import com.qwest.backend.dto.AuthorDTO;
import com.qwest.backend.business.AuthorService;
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

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER')")
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
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER')")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDTO authorDTO) {
        authorDTO.setId(id);
        AuthorDTO updatedAuthor = authorService.update(id, authorDTO);
        if (updatedAuthor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedAuthor);
    }

    @PutMapping(value = "/{id}/avatar", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER')")
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
    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER')")
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
}
package com.qwest.backend.controller;

import com.qwest.backend.business.FileStorageService;
import com.qwest.backend.configuration.exceptionhandler.FileNotFoundException;
import com.qwest.backend.configuration.exceptionhandler.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(value = "/api/images")
@SuppressWarnings("java:S112")
public class ImageUploadController {

    private final FileStorageService fileStorageService;

    @Autowired
    public ImageUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // Endpoint to upload a single image
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileStorageService.uploadFile(file);
            return ResponseEntity.ok(imageUrl);
        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
        }
    }

    // Endpoint to delete an image
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            fileStorageService.deleteFile(imageUrl);
            return ResponseEntity.ok("Image deleted successfully.");
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found: " + e.getMessage());
        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file URL: " + e.getMessage());
        }
    }

    // Endpoint to upload multiple images
    @PostMapping(value = "/upload/multiple", consumes = {"multipart/form-data"})
    public ResponseEntity<List<String>> uploadMultipleImages(@RequestParam("files") List<MultipartFile> files) {
        try {
            List<String> imageUrls = files.stream()
                    .map(file -> {
                        try {
                            return fileStorageService.uploadFile(file);
                        } catch (FileStorageException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
            return ResponseEntity.ok(imageUrls);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof FileStorageException) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of("Failed to upload one or more images: " + cause.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of("Unexpected error occurred: " + e.getMessage()));
        }
    }
}

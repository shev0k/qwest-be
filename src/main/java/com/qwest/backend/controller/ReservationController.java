package com.qwest.backend.controller;

import com.qwest.backend.business.ReservationService;
import com.qwest.backend.dto.ReservationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationDTO reservationDTO) {
        ReservationDTO createdReservation = reservationService.createReservation(reservationDTO);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        return reservationService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByAuthorId(@PathVariable Long authorId) {
        List<ReservationDTO> reservations = reservationService.findByAuthorId(authorId);
        return ResponseEntity.ok(reservations);
    }

    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    @GetMapping("/stay/{stayListingId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByStayListingId(@PathVariable Long stayListingId) {
        List<ReservationDTO> reservations = reservationService.findByStayListingId(stayListingId);
        return ResponseEntity.ok(reservations);
    }

    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.findAll();
        return ResponseEntity.ok(reservations);
    }

    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long id) {
        ReservationDTO cancelledReservation = reservationService.cancelReservation(id);
        return ResponseEntity.ok(cancelledReservation);
    }

    @PreAuthorize("hasAnyRole('FOUNDER', 'HOST', 'TRAVELER', 'PENDING_HOST')")
    @DeleteMapping("/author/{authorId}/canceled")
    public ResponseEntity<Void> deleteCanceledReservations(@PathVariable Long authorId) {
        reservationService.deleteCanceledReservationsByAuthorId(authorId);
        return ResponseEntity.noContent().build();
    }
}

package com.qwest.backend.business;

import com.qwest.backend.dto.ReservationDTO;

import java.util.List;
import java.util.Optional;

public interface ReservationService {
    ReservationDTO createReservation(ReservationDTO reservationDTO);
    Optional<ReservationDTO> findById(Long id);
    List<ReservationDTO> findByAuthorId(Long authorId);
    List<ReservationDTO> findByStayListingId(Long stayListingId);
    List<ReservationDTO> findAll();
    ReservationDTO cancelReservation(Long id);
    void deleteCanceledReservationsByAuthorId(Long authorId);
}

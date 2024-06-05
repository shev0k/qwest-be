package com.qwest.backend.repository;

import com.qwest.backend.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByAuthorId(Long authorId);
    List<Reservation> findByStayListingId(Long stayListingId);
    List<Reservation> findByCancelledFalse();
    List<Reservation> findByAuthorIdAndCancelled(Long authorId, boolean cancelled);
    void deleteByAuthorIdAndCancelledTrue(Long authorId);
}

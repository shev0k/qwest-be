package com.qwest.backend.repository;

import com.qwest.backend.domain.util.BookingCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingCalendarRepository extends JpaRepository<BookingCalendar, Long> {
    List<BookingCalendar> findByStayListingIdAndDateBetween(Long stayListingId, LocalDate startDate, LocalDate endDate);
}

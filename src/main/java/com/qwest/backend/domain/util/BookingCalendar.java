package com.qwest.backend.domain.util;

import com.qwest.backend.domain.StayListing;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BookingCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private Boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    private StayListing stayListing;
}
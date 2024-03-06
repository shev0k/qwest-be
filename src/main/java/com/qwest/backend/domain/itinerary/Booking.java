package com.qwest.backend.domain.itinerary;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_item_id")
    private ItineraryItem itineraryItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id")
    private Itinerary itinerary;

    private Date bookingDate;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private double price;
    private String cancellationPolicy;
    private String contactInfo;
}

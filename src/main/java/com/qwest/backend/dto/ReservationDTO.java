package com.qwest.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReservationDTO {
    private Long id;
    private Long authorId;
    private Long stayListingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer adults;
    private Integer children;
    private Integer infants;
    private Double totalPrice;
    private String bookingCode;
    private boolean cancelled;
    private List<LocalDate> selectedDates;
}

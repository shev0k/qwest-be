package com.qwest.backend.repository.mapper;

import com.qwest.backend.domain.Reservation;
import com.qwest.backend.dto.ReservationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, StayListingMapper.class})
public interface ReservationMapper {

    @Mappings({
            @Mapping(target = "authorId", source = "author.id"),
            @Mapping(target = "stayListingId", source = "stayListing.id"),
            @Mapping(target = "selectedDates", ignore = true) // Custom mapping handled in @AfterMapping
    })
    ReservationDTO toDto(Reservation reservation);

    @Mappings({
            @Mapping(target = "author", ignore = true),
            @Mapping(target = "stayListing", ignore = true)
    })
    Reservation toEntity(ReservationDTO dto);

    @AfterMapping
    default void mapSelectedDates(Reservation reservation, @MappingTarget ReservationDTO reservationDTO) {
        List<LocalDate> selectedDates = new ArrayList<>();
        LocalDate start = reservation.getCheckInDate();
        LocalDate end = reservation.getCheckOutDate();
        while (start != null && end != null && !start.isAfter(end)) {
            selectedDates.add(start);
            start = start.plusDays(1);
        }
        reservationDTO.setSelectedDates(selectedDates);
    }
}

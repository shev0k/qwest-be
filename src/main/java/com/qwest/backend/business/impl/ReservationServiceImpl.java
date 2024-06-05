package com.qwest.backend.business.impl;

import com.qwest.backend.business.NotificationService;
import com.qwest.backend.business.ReservationService;
import com.qwest.backend.business.StayListingService;
import com.qwest.backend.domain.Reservation;
import com.qwest.backend.domain.StayListing;
import com.qwest.backend.domain.Author;
import com.qwest.backend.dto.ReservationDTO;
import com.qwest.backend.repository.ReservationRepository;
import com.qwest.backend.repository.StayListingRepository;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.mapper.ReservationMapper;
import com.qwest.backend.domain.util.BookingCodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final StayListingRepository stayListingRepository;
    private final AuthorRepository authorRepository;
    private final ReservationMapper reservationMapper;
    private final StayListingService stayListingService;
    private final NotificationService notificationService;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  StayListingRepository stayListingRepository,
                                  AuthorRepository authorRepository,
                                  ReservationMapper reservationMapper,
                                  StayListingService stayListingService,
                                  NotificationService notificationService) {
        this.reservationRepository = reservationRepository;
        this.stayListingRepository = stayListingRepository;
        this.authorRepository = authorRepository;
        this.reservationMapper = reservationMapper;
        this.stayListingService = stayListingService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        StayListing stayListing = stayListingRepository.findById(reservationDTO.getStayListingId())
                .orElseThrow(() -> new EntityNotFoundException("Stay listing not found with id " + reservationDTO.getStayListingId()));
        Author author = authorRepository.findById(reservationDTO.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + reservationDTO.getAuthorId()));

        Reservation reservation = reservationMapper.toEntity(reservationDTO);
        reservation.setStayListing(stayListing);
        reservation.setAuthor(author);
        reservation.setBookingCode(BookingCodeGenerator.generateUniqueCode());

        Reservation savedReservation = reservationRepository.save(reservation);
        ReservationDTO savedReservationDTO = reservationMapper.toDto(savedReservation);

        List<LocalDate> selectedDates = reservationDTO.getSelectedDates();
        stayListingService.updateAvailableDates(reservationDTO.getStayListingId(), selectedDates);

        notificationService.notifyReservation(stayListing.getAuthor().getId(), reservation.getAuthor().getId(), "", stayListing.getId());

        return savedReservationDTO;
    }

    @Override
    public Optional<ReservationDTO> findById(Long id) {
        return reservationRepository.findById(id).map(reservationMapper::toDto);
    }

    @Override
    public List<ReservationDTO> findByAuthorId(Long authorId) {
        return reservationRepository.findByAuthorId(authorId).stream()
                .map(reservationMapper::toDto).toList();
    }

    @Override
    public List<ReservationDTO> findByStayListingId(Long stayListingId) {
        return reservationRepository.findByStayListingId(stayListingId).stream()
                .map(reservationMapper::toDto).toList();
    }

    @Override
    public List<ReservationDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto).toList();
    }

    @Override
    @Transactional
    public ReservationDTO cancelReservation(Long id) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setCancelled(true);
            Reservation savedReservation = reservationRepository.save(reservation);
            ReservationDTO savedReservationDTO = reservationMapper.toDto(savedReservation);

            stayListingService.removeUnavailableDates(reservation.getStayListing().getId(),
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate());

            notificationService.notifyReservationCancellation(reservation.getStayListing().getAuthor().getId(), reservation.getAuthor().getId(), "", reservation.getStayListing().getId());

            return savedReservationDTO;
        }).orElseThrow(() -> new IllegalStateException("Reservation not found with id " + id));
    }

    @Override
    @Transactional
    public void deleteCanceledReservationsByAuthorId(Long authorId) {
        List<Reservation> canceledReservations = reservationRepository.findByAuthorIdAndCancelled(authorId, true);
        reservationRepository.deleteAll(canceledReservations);
    }
}

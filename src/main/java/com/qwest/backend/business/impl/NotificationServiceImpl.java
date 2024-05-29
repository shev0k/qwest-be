package com.qwest.backend.business.impl;

import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.Notification;
import com.qwest.backend.domain.util.AuthorRole;
import com.qwest.backend.dto.NotificationDTO;
import com.qwest.backend.repository.NotificationRepository;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.mapper.NotificationMapper;
import com.qwest.backend.business.NotificationService;
import com.qwest.backend.business.WebSocketNotificationService;
import com.qwest.backend.domain.util.NotificationUtils;
import com.qwest.backend.domain.util.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthorRepository authorRepository;
    private final NotificationMapper notificationMapper;
    private final WebSocketNotificationService webSocketNotificationService;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, AuthorRepository authorRepository,
                                   NotificationMapper notificationMapper, WebSocketNotificationService webSocketNotificationService) {
        this.notificationRepository = notificationRepository;
        this.authorRepository = authorRepository;
        this.notificationMapper = notificationMapper;
        this.webSocketNotificationService = webSocketNotificationService;
    }

    @Override
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = notificationMapper.toEntity(notificationDTO);
        Author author = authorRepository.findById(notificationDTO.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid author ID"));
        notification.setAuthor(author);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        Notification savedNotification = notificationRepository.save(notification);
        NotificationDTO savedNotificationDTO = notificationMapper.toDto(savedNotification);

        webSocketNotificationService.sendNotification(savedNotificationDTO);

        return savedNotificationDTO;
    }

    @Override
    public List<NotificationDTO> getNotificationsForAuthor(Long authorId) {
        return notificationRepository.findByAuthorIdOrderByTimestampDesc(authorId).stream()
                .map(notification -> {
                    NotificationDTO dto = notificationMapper.toDto(notification);
                    dto.setMessage(NotificationUtils.formatMessage(notification.getType(), notification.getMessage()));
                    dto.setTimestampFormatted(TimestampUtils.formatTimestamp(notification.getTimestamp()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markNotificationsAsRead(List<Long> notificationIds) {
        notificationRepository.markNotificationsAsRead(notificationIds);
    }

    private void createAndSendNotification(Long authorId, String message, String type) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setAuthorId(authorId);
        notificationDTO.setMessage(message);
        notificationDTO.setTimestamp(LocalDateTime.now());
        notificationDTO.setType(type);
        notificationDTO.setRead(false);

        createNotification(notificationDTO);
    }

    @Override
    public void notifyHostRequest(Long authorId) {
        List<Author> founders = authorRepository.findByRole(AuthorRole.FOUNDER);
        String message = NotificationUtils.formatMessage("REQUEST_TO_HOST");
        for (Author founder : founders) {
            createAndSendNotification(founder.getId(), message, "REQUEST_TO_HOST");
        }
    }

    @Override
    public void notifyHostApproval(Long authorId) {
        createAndSendNotification(authorId, NotificationUtils.formatMessage("BECOME_HOST"), "BECOME_HOST");
    }

    @Override
    public void notifyHostRejection(Long authorId) {
        createAndSendNotification(authorId, NotificationUtils.formatMessage("REJECT_HOST"), "REJECT_HOST");
    }

    @Override
    public void notifyDemotionToTraveler(Long authorId) {
        createAndSendNotification(authorId, NotificationUtils.formatMessage("DEMOTE_TRAVELER"), "DEMOTE_TRAVELER");
    }

    @Override
    public void notifyStayReview(Long authorId, Long reviewerId, String message) {
        Author reviewer = authorRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reviewer ID"));
        createAndSendNotification(authorId, NotificationUtils.formatMessage("STAY_REVIEW", reviewer.getFirstName() + " " + reviewer.getLastName()), "STAY_REVIEW");
    }

    @Override
    public void notifyReservation(Long authorId, Long reserverId, String message) {
        Author reserver = authorRepository.findById(reserverId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reserver ID"));
        createAndSendNotification(authorId, NotificationUtils.formatMessage("RESERVATION", reserver.getFirstName() + " " + reserver.getLastName(), message), "RESERVATION");
    }

    @Override
    public void notifyReservationCancellation(Long authorId, Long reserverId, String message) {
        Author reserver = authorRepository.findById(reserverId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reserver ID"));
        createAndSendNotification(authorId, NotificationUtils.formatMessage("CANCEL_RESERVATION", reserver.getFirstName() + " " + reserver.getLastName(), message), "CANCEL_RESERVATION");
    }
}
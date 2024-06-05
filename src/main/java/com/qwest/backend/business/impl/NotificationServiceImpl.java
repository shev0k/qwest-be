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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        Author sender = authorRepository.findById(notificationDTO.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));

        notification.setAuthor(author);
        notification.setSender(sender);
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
                .toList();
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.markNotificationAsRead(notificationId);
    }

    @Override
    @Transactional
    public void markNotificationsAsRead(List<Long> notificationIds) {
        notificationRepository.markNotificationsAsRead(notificationIds);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsReadForAuthor(Long authorId) {
        List<Long> notificationIds = notificationRepository.findByAuthorIdOrderByTimestampDesc(authorId)
                .stream()
                .map(Notification::getId)
                .toList();
        notificationRepository.markNotificationsAsRead(notificationIds);
    }

    @Override
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAllNotifications() {
        notificationRepository.deleteAll();
    }

    @Override
    @Transactional
    public void deleteNotificationsForAuthor(Long authorId) {
        notificationRepository.deleteByAuthorId(authorId);
    }

    private void createAndSendNotification(Long authorId, Long senderId, String message, String type, Long stayId) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setAuthorId(authorId);
        notificationDTO.setSenderId(senderId);
        notificationDTO.setMessage(message);
        notificationDTO.setTimestamp(LocalDateTime.now());
        notificationDTO.setType(type);
        notificationDTO.setRead(false);
        notificationDTO.setStayId(stayId);

        createNotification(notificationDTO);
    }

    @Override
    public void notifyHostRequest(Long authorId) {
        List<Author> founders = authorRepository.findByRole(AuthorRole.FOUNDER);
        String message = NotificationUtils.formatMessage("REQUEST_TO_HOST");
        for (Author founder : founders) {
            createAndSendNotification(founder.getId(), authorId, message, "REQUEST_TO_HOST", null);
        }
    }

    @Override
    public void notifyHostApproval(Long authorId) {
        createAndSendNotification(authorId, 1L, NotificationUtils.formatMessage("BECOME_HOST"), "BECOME_HOST", null);
    }

    @Override
    public void notifyHostRejection(Long authorId) {
        createAndSendNotification(authorId, 1L, NotificationUtils.formatMessage("REJECT_HOST"), "REJECT_HOST", null);
    }

    @Override
    public void notifyDemotionToTraveler(Long authorId) {
        createAndSendNotification(authorId, 1L, NotificationUtils.formatMessage("DEMOTE_TRAVELER"), "DEMOTE_TRAVELER", null);
    }

    @Override
    public void notifyStayReview(Long authorId, Long reviewerId, String message, Long stayId) {
        Author reviewer = authorRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reviewer ID"));
        createAndSendNotification(authorId, reviewerId, NotificationUtils.formatMessage("STAY_REVIEW", reviewer.getFirstName() + " " + reviewer.getLastName()), "STAY_REVIEW", stayId);
    }

    @Override
    public void notifyReservation(Long authorId, Long reserverId, String message, Long stayId) {
        Author reserver = authorRepository.findById(reserverId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reserver ID"));
        createAndSendNotification(authorId, reserverId, NotificationUtils.formatMessage("RESERVATION", reserver.getFirstName() + " " + reserver.getLastName(), message), "RESERVATION", stayId);
    }

    @Override
    public void notifyReservationCancellation(Long authorId, Long reserverId, String message, Long stayId) {
        Author reserver = authorRepository.findById(reserverId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reserver ID"));
        createAndSendNotification(authorId, reserverId, NotificationUtils.formatMessage("CANCEL_RESERVATION", reserver.getFirstName() + " " + reserver.getLastName(), message), "CANCEL_RESERVATION", stayId);
    }
}

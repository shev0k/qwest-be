package com.qwest.backend.business;

import com.qwest.backend.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    NotificationDTO createNotification(NotificationDTO notificationDTO);
    List<NotificationDTO> getNotificationsForAuthor(Long authorId);
    List<NotificationDTO> getAllNotifications();
    void deleteAllNotifications();
    void deleteNotificationsForAuthor(Long authorId);
    void markNotificationAsRead(Long notificationId);
    void markNotificationsAsRead(List<Long> notificationIds);
    void markAllNotificationsAsReadForAuthor(Long authorId);
    void notifyHostRequest(Long authorId);
    void notifyHostApproval(Long authorId);
    void notifyHostRejection(Long authorId);
    void notifyDemotionToTraveler(Long authorId);
    void notifyStayReview(Long authorId, Long reviewerId, String message, Long stayId);
    void notifyReservation(Long authorId, Long reserverId, String message, Long stayId);
    void notifyReservationCancellation(Long authorId, Long reserverId, String message, Long stayId);
}

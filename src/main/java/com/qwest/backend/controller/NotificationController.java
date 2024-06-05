package com.qwest.backend.controller;

import com.qwest.backend.dto.NotificationDTO;
import com.qwest.backend.business.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = "Authorization")
@RestController
@RequestMapping("api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDTO) {
        NotificationDTO createdNotification = notificationService.createNotification(notificationDTO);
        return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsForAuthor(@PathVariable Long authorId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsForAuthor(authorId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllNotifications() {
        notificationService.deleteAllNotifications();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/author/{authorId}")
    public ResponseEntity<Void> deleteNotificationsForAuthor(@PathVariable Long authorId) {
        notificationService.deleteNotificationsForAuthor(authorId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/read")
    public ResponseEntity<Void> markNotificationsAsRead(@RequestBody List<Long> notificationIds) {
        notificationService.markNotificationsAsRead(notificationIds);
        return ResponseEntity.noContent().build();
    }

    // New endpoint
    @PutMapping("/author/{authorId}/read-all")
    public ResponseEntity<Void> markAllNotificationsAsReadForAuthor(@PathVariable Long authorId) {
        notificationService.markAllNotificationsAsReadForAuthor(authorId);
        return ResponseEntity.noContent().build();
    }
}

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

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/read")
    public ResponseEntity<Void> markNotificationsAsRead(@RequestBody List<Long> notificationIds) {
        notificationService.markNotificationsAsRead(notificationIds);
        return ResponseEntity.noContent().build();
    }
}

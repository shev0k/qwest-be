package com.qwest.backend.business;

import com.qwest.backend.dto.ChangeNotificationDTO;
import com.qwest.backend.dto.NotificationDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(NotificationDTO notification) {
        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getAuthorId(), notification);
    }

    public void broadcastChange(String type, Object payload) {
        messagingTemplate.convertAndSend("/topic/changes", new ChangeNotificationDTO(type, payload));
    }
}

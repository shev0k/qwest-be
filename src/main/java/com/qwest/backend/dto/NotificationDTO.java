package com.qwest.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDTO {
    private Long id;
    private Long authorId;
    private String message;
    private LocalDateTime timestamp;
    private String type;
    private boolean isRead;
    private String timestampFormatted;
}

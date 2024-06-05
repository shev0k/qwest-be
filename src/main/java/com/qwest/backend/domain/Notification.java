package com.qwest.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_author_id", columnList = "author_id"),
        @Index(name = "idx_sender_id", columnList = "sender_id"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Author sender;

    private String message;
    private LocalDateTime timestamp;
    private String type;

    private boolean isRead;
    private Long stayId;
}

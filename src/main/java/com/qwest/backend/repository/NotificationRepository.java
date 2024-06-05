package com.qwest.backend.repository;

import com.qwest.backend.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAuthorIdOrderByTimestampDesc(Long authorId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :ids")
    void markNotificationsAsRead(List<Long> ids);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId")
    void markNotificationAsRead(Long notificationId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.author.id = :authorId")
    void deleteByAuthorId(Long authorId);

}

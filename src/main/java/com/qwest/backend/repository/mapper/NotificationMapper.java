package com.qwest.backend.repository.mapper;

import com.qwest.backend.domain.Notification;
import com.qwest.backend.dto.NotificationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mappings({
            @Mapping(target = "authorId", source = "author.id"),
            @Mapping(target = "senderId", source = "sender.id"),
            @Mapping(target = "senderName", source = "sender.username"),
            @Mapping(target = "senderAvatar", source = "sender.avatar"),
            @Mapping(target = "timestampFormatted", ignore = true)
    })
    NotificationDTO toDto(Notification notification);

    @Mappings({
            @Mapping(target = "author", ignore = true),
            @Mapping(target = "sender", ignore = true)
    })
    Notification toEntity(NotificationDTO notificationDTO);

    @AfterMapping
    default void mapTimestampFormatted(Notification notification, @MappingTarget NotificationDTO notificationDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (notification.getTimestamp() != null) {
            notificationDTO.setTimestampFormatted(notification.getTimestamp().format(formatter));
        }
    }
}

package com.qwest.backend.repository.mapper;

import com.qwest.backend.domain.Notification;
import com.qwest.backend.dto.NotificationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "authorId", source = "author.id")
    NotificationDTO toDto(Notification notification);

    @Mapping(target = "author", ignore = true)
    Notification toEntity(NotificationDTO notificationDTO);
}

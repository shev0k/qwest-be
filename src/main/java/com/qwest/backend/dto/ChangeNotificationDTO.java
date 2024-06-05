package com.qwest.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeNotificationDTO {
    private String type;
    private Object payload;

    public ChangeNotificationDTO(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }
}

package com.qwest.backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String displayName;
    private String avatar;
    private String bgImage;
    private String email;
    private Integer count;
    private String desc;
    private String jobName;
    private Double starRating;
}

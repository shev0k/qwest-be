package com.qwest.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Set;

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
    private String description;
    private Double starRating;
    private String role;

    private Set<Long> stayListingIds;

    private String password;
    private String confirmPassword;

    private String jwt;
}

package com.qwest.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter @Setter
public class AuthorDTO {
    private Long id;

    private String password;
    private String confirmPassword;

    @Email(message = "Email should be valid.")
    @NotBlank(message = "Email must not be empty.")
    private String email;

    private String firstName;
    private String lastName;
    private String username;

    private String avatar;

    private String country;
    private String phoneNumber;
    private String description;

    private Integer count;
    private Double starRating;

    private String role;

    private Set<Long> stayListingIds;
    private Set<Long> wishlistIds;

    private String jwt;
}

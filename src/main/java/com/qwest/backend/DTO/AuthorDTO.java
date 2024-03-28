package com.qwest.backend.DTO;

import com.qwest.backend.domain.StayListing;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
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
    private String jobName;
    private Double starRating;

    private Set<Long> stayListingIds;
}

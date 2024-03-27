package com.qwest.backend.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StayListing> stayListings = new HashSet<>();
}

package com.qwest.backend.domain;

import com.qwest.backend.domain.util.AuthorRole;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String passwordHash;

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

    @Enumerated(EnumType.STRING)
    private AuthorRole role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<StayListing> stayListings = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "author_wishlist",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "stay_listing_id"))
    private Set<StayListing> wishlist = new HashSet<>();
}

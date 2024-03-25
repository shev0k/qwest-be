package com.qwest.backend.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Taxonomy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String href;
    private Integer count;
    private String thumbnail;
    private String description;
    private String color;

    @Enumerated(EnumType.STRING)
    private TaxonomyType taxonomy;

    @ManyToMany(mappedBy = "categories")
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "listingCategory")
    private Set<StayListing> stayListings = new HashSet<>();

    @OneToMany(mappedBy = "listingCategory")
    private Set<ExperienceListing> experienceListings = new HashSet<>();
}

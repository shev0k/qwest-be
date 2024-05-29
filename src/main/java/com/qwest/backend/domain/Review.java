package com.qwest.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ManyToOne
    @JoinColumn(name = "stay_listing_id", nullable = false)
    private StayListing stayListing;

    private int rating;
    private String comment;
    private LocalDate createdAt;

    public Review() {
        this.createdAt = LocalDate.now();
    }
}
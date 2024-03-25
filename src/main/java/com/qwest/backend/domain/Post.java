package com.qwest.backend.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    private String date;
    private String href;
    private String title;
    private String featuredImage;
    private String description;
    private Integer commentCount;
    private Integer viewedCount;
    private Integer readingTime;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @ManyToMany
    @JoinTable(
            name = "post_taxonomy",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "taxonomy_id")
    )
    private Set<Taxonomy> categories = new HashSet<>();
}

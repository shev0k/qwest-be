package com.qwest.backend.domain;

import com.qwest.backend.domain.util.AuthorRole;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Getter
@Setter
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String displayName;
    private String avatar;
    private String bgImage;
    private String email;
    private Integer count;
    private String description;
    private Double starRating;

    @Enumerated(EnumType.STRING)
    private AuthorRole role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StayListing> stayListings = new HashSet<>();

    public void setPassword(String password) {
        this.passwordHash = new BCryptPasswordEncoder().encode(password);
    }

    public boolean canAcceptAuthors() {
        return AuthorRole.FOUNDER.equals(this.role);
    }
}

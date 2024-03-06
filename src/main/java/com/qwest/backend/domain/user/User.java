package com.qwest.backend.domain.user;

import com.qwest.backend.domain.itinerary.Itinerary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String profileImage;

    @Temporal(TemporalType.TIMESTAMP)
    private Date joinDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private List<Role> roles = new ArrayList<>();

    @Lob
    private String preferences;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Itinerary> itineraries = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password, String profileImage, List<Role> roles, String preferences) {
        this(username, email, password);
        this.profileImage = profileImage;
        this.roles = roles;
        this.preferences = preferences;
    }

}

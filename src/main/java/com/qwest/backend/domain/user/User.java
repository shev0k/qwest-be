package com.qwest.backend.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;


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

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password, String profileImage) {
        this(username, email, password);
        this.profileImage = profileImage;
    }

}

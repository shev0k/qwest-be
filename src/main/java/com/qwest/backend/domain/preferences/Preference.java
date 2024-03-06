package com.qwest.backend.domain.preferences;

import com.qwest.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "preferences")
@Getter
@Setter
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PreferenceType type;

    private String value;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Preference() {}

    public Preference(PreferenceType type, String value, User user) {
        this.type = type;
        this.value = value;
        this.user = user;
    }
}

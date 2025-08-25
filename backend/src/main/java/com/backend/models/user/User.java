package com.backend.models.user;

import com.backend.models.hike.Hike;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Double rating;

    @OneToMany(mappedBy = "organizer")
    private List<Hike> organizedHikes;

    @ManyToMany(mappedBy = "participants")
    private List<Hike> participatedHikes;

    private boolean enabled = false;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonExpired = true;
}

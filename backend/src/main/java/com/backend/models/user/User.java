package com.backend.models.user;

import com.backend.models.hike.Hike;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProvider> providers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Role role;

    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Double rating;

    private String bio;
    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;
    private Double weight;
    private Double height;
    private Boolean hasMedicalConditions;
    private String medicalNotes;
    private String phoneNumber;

    @OneToMany(mappedBy = "organizer")
    private List<Hike> organizedHikes;

    @ManyToMany(mappedBy = "participants")
    private List<Hike> participatedHikes;

    private boolean enabled = false;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonExpired = true;
}

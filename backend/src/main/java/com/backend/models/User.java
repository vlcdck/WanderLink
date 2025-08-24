package com.backend.models;

import com.backend.enums.Role;
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

    private Role role;

    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Double rating;

    @OneToMany(mappedBy = "organizer")
    private List<Hike> organizedHikes;

    @ManyToMany(mappedBy = "participants")
    private List<Hike> participatedHikes;

}

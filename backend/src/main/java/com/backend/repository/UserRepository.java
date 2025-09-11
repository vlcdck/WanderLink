package com.backend.repository;

import com.backend.models.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username); // add yo to register

    @EntityGraph(attributePaths = "participatedHikes")
    Optional<User> findWithParticipatedHikesById(Long id);

    @EntityGraph(attributePaths = "organizedHikes")
    Optional<User> findWithOrganizedHikesById(Long id);
}

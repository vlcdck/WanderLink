package com.backend.repository;

import com.backend.models.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true where rt.user.id = :userId")
    void revokeAllByUserId(@Param("userId") Long userId);

    void deleteAllByExpiresAtBefore(LocalDateTime now);
}

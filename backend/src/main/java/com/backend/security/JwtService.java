package com.backend.security;

import com.backend.exeptions.InvalidTokenException;
import com.backend.models.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessExpMinutes;


    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-exp-min}") long accessExpMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMinutes = accessExpMinutes;
    }

    public String generateAccessToken(Long userId, String email, Role role) {
        Instant now = Instant.now();
        Map<String, Object> claims = Map.of(
                "role", role.name(),
                "email", email,
                "jti", UUID.randomUUID().toString()
        );


        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuer("wanderlink-api")
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(accessExpMinutes, ChronoUnit.MINUTES)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (InvalidTokenException e) {
            return false;
        }
    }


    public Long extractUserId(String token) {
        return Long.valueOf(parseToken(token).getSubject());
    }


    public String extractEmail(String token) {
        Object email = parseToken(token).get("email");
        return email != null ? email.toString() : null;
    }


    public String extractRole(String token) {
        Object role = parseToken(token).get("role");
        return role != null ? role.toString() : null;
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
//                    .requireIssuer("wanderlink-api")
                    .setAllowedClockSkewSeconds(60) // невеликий запас для розбіжностей часу
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }
}

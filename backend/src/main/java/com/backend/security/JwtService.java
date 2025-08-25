package com.backend.security;

import com.backend.models.user.Role;
import io.jsonwebtoken.Claims;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessExpMinutes;
    private final long refreshExpDays;


    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-exp-min}") long accessExpMinutes,
            @Value("${jwt.refresh-exp-days}") long refreshExpDays
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMinutes = accessExpMinutes;
        this.refreshExpDays = refreshExpDays;
    }

    public String generateAccessToken(Long userId, String email, Role role) {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());
        claims.put("email", email);


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(accessExpMinutes, ChronoUnit.MINUTES)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshExpDays, ChronoUnit.DAYS)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return Long.valueOf(claims.getSubject());
    }


    public String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        Object email = claims.get("email");
        return email != null ? email.toString() : null;
    }


    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        Object role = claims.get("role");
        return role != null ? role.toString() : null;
    }
}

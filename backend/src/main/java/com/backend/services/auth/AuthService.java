package com.backend.services.auth;

import com.backend.dto.auth.AuthResponse;
import com.backend.dto.auth.LoginRequest;
import com.backend.dto.auth.RegisterRequest;
import com.backend.models.token.ConfirmationToken;
import com.backend.models.user.Role;
import com.backend.models.user.User;
import com.backend.repository.ConfirmationTokenRepository;
import com.backend.repository.UserRepository;
import com.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final ConfirmationTokenRepository tokenRepository;

    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setRole(Role.TOURIST);
        user.setEnabled(false);

        userRepository.save(user);

        ConfirmationToken token = new ConfirmationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(12));

        tokenRepository.save(token);

        emailService.sendConfirmationEmail(user.getEmail(), token.getToken());
    }

    public AuthResponse confirm(String token) {
        ConfirmationToken confirmation = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (confirmation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = confirmation.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        tokenRepository.delete(confirmation);

        String access = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refresh = jwtService.generateRefreshToken(user.getId());

        return new AuthResponse(access, refresh);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not confirmed");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String access = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refresh = jwtService.generateRefreshToken(user.getId());

        return new AuthResponse(access, refresh);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        Long userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String access = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String newRefresh = jwtService.generateRefreshToken(user.getId());

        return new AuthResponse(access, newRefresh);
    }
}

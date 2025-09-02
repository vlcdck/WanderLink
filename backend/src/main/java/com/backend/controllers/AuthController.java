package com.backend.controllers;

import com.backend.dto.auth.AuthResponse;
import com.backend.dto.auth.LoginRequest;
import com.backend.dto.auth.RefreshTokenRequest;
import com.backend.dto.auth.RegisterRequest;
import com.backend.services.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req, Locale locale) {
        authService.register(req, locale);
        return ResponseEntity.ok("Check your email for confirmation link");
    }

    @GetMapping("/confirm")
    public ResponseEntity<AuthResponse> confirm(@RequestParam String token) {
        AuthResponse response = authService.confirm(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<Void> resendConfirmation(@RequestParam String email, Locale locale) {
        authService.resendConfirmation(email, locale);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse response = authService.login(req);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest req) {
        authService.revokeRefreshToken(req.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest req) {
        AuthResponse response = authService.refresh(req.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}


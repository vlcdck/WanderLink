package com.backend.controllers;

import com.backend.dto.auth.AuthResponse;
import com.backend.dto.auth.LoginRequest;
import com.backend.dto.auth.RegisterRequest;
import com.backend.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok("Check your email for confirmation link");
    }

    @GetMapping("/confirm")
    public ResponseEntity<AuthResponse> confirm(@RequestParam String token) {
        return ResponseEntity.ok(authService.confirm(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.refresh(body.get("refreshToken")));
    }
}


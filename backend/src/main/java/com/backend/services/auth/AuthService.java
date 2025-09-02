package com.backend.services.auth;

import com.backend.dto.auth.AuthResponse;
import com.backend.dto.auth.LoginRequest;
import com.backend.dto.auth.RegisterRequest;
import com.backend.exeptions.*;
import com.backend.mappers.UserMapper;
import com.backend.models.token.ConfirmationToken;
import com.backend.models.token.RefreshToken;
import com.backend.models.user.Role;
import com.backend.models.user.User;
import com.backend.repository.ConfirmationTokenRepository;
import com.backend.repository.RefreshTokenRepository;
import com.backend.repository.UserRepository;
import com.backend.security.JwtService;
import com.backend.security.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final ConfirmationTokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-exp-days}")
    private long refreshExpDays;

    public void register(RegisterRequest req, Locale locale) {
        if (userRepository.existsByEmail(req.getEmail())) throw new EmailAlreadyUsedException();

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

        emailService.sendConfirmationEmail(user.getEmail(), token.getToken(), locale);
    }

    public AuthResponse confirm(String token) {
        ConfirmationToken confirmation = tokenRepository.findByToken(token)
                .orElseThrow(InvalidTokenException::new);

        if (confirmation.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ExpiredTokenException();

        User user = confirmation.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(confirmation);

        return generateTokens(user);
    }

    @Transactional
    public void resendConfirmation(String email, Locale locale) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.isEnabled()) {
            throw new AlreadyConfirmedException();
        }

        // Можна: видалити старі токени для цього юзера, щоб не плодити
        tokenRepository.deleteAllByUserId(user.getId());

        ConfirmationToken token = new ConfirmationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(12));
        tokenRepository.save(token);

        emailService.sendConfirmationEmail(user.getEmail(), token.getToken(), locale);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        User user = ((UserPrincipal) auth.getPrincipal()).getUser();

        if (!user.isEnabled()) throw new AccountNotConfirmedException();

        refreshTokenRepository.revokeAllByUserId(user.getId());
        return generateTokens(user);
    }

    public AuthResponse refresh(String rawRefreshToken) {
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash(rawRefreshToken))
                .orElseThrow(InvalidTokenException::new);

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RefreshTokenInvalidException();

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return generateTokens(stored.getUser());
    }

    public void revokeRefreshToken(String rawRefreshToken) {
        refreshTokenRepository.findByTokenHash(hash(rawRefreshToken))
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private AuthResponse generateTokens(User user) {
        String access = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String rawRefresh = UUID.randomUUID() + "." + UUID.randomUUID();

        System.out.println("Generated tokens: access=" + access + ", refresh=" + rawRefresh);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(hash(rawRefresh));
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshExpDays));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        // Мапимо юзера на DTO
        return new AuthResponse(access, rawRefresh, UserMapper.toDTO(user));
    }

    private String hash(String raw) {
        return DigestUtils.sha256Hex(raw);
    }
}

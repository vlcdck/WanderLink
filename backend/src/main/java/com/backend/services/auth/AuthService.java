package com.backend.services.auth;

import com.backend.dto.auth.AuthResponse;
import com.backend.dto.auth.LoginRequest;
import com.backend.dto.auth.RegisterRequest;
import com.backend.exeptions.*;
import com.backend.mappers.UserMapper;
import com.backend.models.token.ConfirmationToken;
import com.backend.models.token.RefreshToken;
import com.backend.models.user.AuthProvider;
import com.backend.models.user.Role;
import com.backend.models.user.User;
import com.backend.models.user.UserProvider;
import com.backend.repository.ConfirmationTokenRepository;
import com.backend.repository.RefreshTokenRepository;
import com.backend.repository.UserRepository;
import com.backend.security.JwtService;
import com.backend.security.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

    /**
     * Логін або реєстрація через Google
     */
    @Transactional
    public AuthResponse loginOrRegisterGoogle(String email, String fullName, String googleId) {
        User user = userRepository.findByEmail(email).orElseGet(() -> createGoogleUser(email, fullName, googleId));

        // Якщо акаунт вже існує, але без Google → додаємо Google як провайдера
        addProviderIfMissing(user, AuthProvider.GOOGLE, googleId);

        revokeAllTokens(user);
        return generateTokens(user);
    }

    /**
     * Звичайна реєстрація
     */
    @Transactional
    public void register(RegisterRequest req, Locale locale) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyUsedException();
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setRole(Role.TOURIST);
        user.setEnabled(false); // треба підтвердити email

        addProvider(user, AuthProvider.LOCAL, null);
        userRepository.save(user);

        createAndSendConfirmationToken(user, locale);
    }

    /**
     * Підтвердження акаунта
     */
    public AuthResponse confirm(String token) {
        ConfirmationToken confirmation = tokenRepository.findByToken(token)
                .orElseThrow(InvalidTokenException::new);

        if (confirmation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException();
        }

        User user = confirmation.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(confirmation);

        return generateTokens(user);
    }

    /**
     * Повторна відправка підтвердження
     */
    @Transactional
    public void resendConfirmation(String email, Locale locale) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.isEnabled()) {
            throw new AlreadyConfirmedException();
        }

        tokenRepository.deleteAllByUserId(user.getId());
        createAndSendConfirmationToken(user, locale);
    }

    /**
     * Логін локального користувача
     */
    @Transactional
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserNotFoundException(req.getEmail()));

        // Якщо акаунт тільки з Google → не даємо логінитись локально
        boolean googleOnly = user.getPassword() == null &&
                user.getProviders().stream().anyMatch(p -> p.getProvider() == AuthProvider.GOOGLE);

        if (googleOnly) {
            throw new LoginWithGoogleOnlyException("Акаунт створено через Google. Використовуйте Google login.");
        }

        // Валідація пароля
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User authenticatedUser = ((UserPrincipal) auth.getPrincipal()).getUser();

        if (!authenticatedUser.isEnabled()) {
            throw new AccountNotConfirmedException();
        }

        return loginExistingUser(authenticatedUser);
    }

    /**
     * Refresh токена
     */
    public AuthResponse refresh(String rawRefreshToken) {
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash(rawRefreshToken))
                .orElseThrow(InvalidTokenException::new);

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenInvalidException();
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return generateTokens(stored.getUser());
    }

    /**
     * Відкликання refresh токена
     */
    public void revokeRefreshToken(String rawRefreshToken) {
        refreshTokenRepository.findByTokenHash(hash(rawRefreshToken))
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    // ==================== PRIVATE HELPERS ====================

    private User createGoogleUser(String email, String fullName, String googleId) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(email.split("@")[0]);
        newUser.setFirstName(fullName.split(" ")[0]);
        newUser.setLastName(fullName.contains(" ") ? fullName.split(" ")[1] : "");
        newUser.setRole(Role.TOURIST);
        newUser.setEnabled(true); // OAuth користувач відразу активний
        newUser.setPassword(null);

        addProvider(newUser, AuthProvider.GOOGLE, googleId);
        return userRepository.save(newUser);
    }

    private void createAndSendConfirmationToken(User user, Locale locale) {
        ConfirmationToken token = new ConfirmationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(12));
        tokenRepository.save(token);

        emailService.sendConfirmationEmail(user.getEmail(), token.getToken(), locale);
    }

    private void addProvider(User user, AuthProvider provider, String providerUserId) {
        UserProvider userProvider = UserProvider.builder()
                .provider(provider)
                .providerUserId(providerUserId)
                .user(user)
                .build();
        user.getProviders().add(userProvider);
    }

    private void addProviderIfMissing(User user, AuthProvider provider, String providerUserId) {
        boolean exists = user.getProviders().stream()
                .anyMatch(p -> p.getProvider() == provider);

        if (!exists) {
            addProvider(user, provider, providerUserId);
            userRepository.save(user);
        }
    }

    private void revokeAllTokens(User user) {
        refreshTokenRepository.revokeAllByUserId(user.getId());
    }

    private AuthResponse loginExistingUser(User user) {
        revokeAllTokens(user);
        return generateTokens(user);
    }

    private AuthResponse generateTokens(User user) {
        String access = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String rawRefresh = UUID.randomUUID() + "." + UUID.randomUUID();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(hash(rawRefresh));
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshExpDays));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
        log.info("🔑 Tokens generated for user {}", user.getEmail());

        return new AuthResponse(access, rawRefresh, UserMapper.toDTO(user));
    }

    private String hash(String raw) {
        return DigestUtils.sha256Hex(raw);
    }
}

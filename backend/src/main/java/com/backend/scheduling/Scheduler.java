package com.backend.scheduling;

import com.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 1 ? * MON")
    public void cleanExpiredRefreshTokens() {
        refreshTokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }
}

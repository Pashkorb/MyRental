package com.example.Security.service;

import com.example.Security.model.RefreshToken;
import com.example.Security.model.Users;
import com.example.Security.repo.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTService jwtService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JWTService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public RefreshToken createRefreshToken(Users user) {
        // Проверяем существующий токен
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        RefreshToken refreshToken;
        if (existingToken.isPresent()) {
            // Обновляем существующий токен
            refreshToken = existingToken.get();
            refreshToken.setToken(jwtService.generateRefreshToken(user.getLogin(),user.getId()));
            refreshToken.setExpiryDate(Instant.now().plus(35, ChronoUnit.DAYS));
        } else {
            // Создаем новый токен
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(jwtService.generateRefreshToken(user.getLogin(),user.getId()));
            refreshToken.setExpiryDate(Instant.now().plus(35, ChronoUnit.DAYS));
        }

        return refreshTokenRepository.save(refreshToken);
    }
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }

    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    public Optional<RefreshToken> findByUser(Users user) {
        return refreshTokenRepository.findByUser(user);
    }
}
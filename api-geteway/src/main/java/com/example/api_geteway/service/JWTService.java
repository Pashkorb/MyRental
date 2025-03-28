package com.example.api_geteway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    // Метод для получения ключа подписи
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Метод для парсинга токена и возврата Claims
    public Claims parseToken(String token) {
        try {
            // Используем parserBuilder для парсинга токена
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // Устанавливаем ключ
                    .build()
                    .parseClaimsJws(token); // Парсим токен

            return claimsJws.getBody(); // Возвращаем Claims (тело токена)
        } catch (JwtException | IllegalArgumentException e) {
            // Логируем ошибку и выбрасываем исключение
            throw new IllegalArgumentException("Неверный или просроченный JWT-токен", e);
        }
    }

    // Метод для извлечения username из токена
    public String extractUserName(String token) {
        return parseToken(token).getSubject();
    }

    // Метод для проверки истечения срока действия токена
    public boolean isTokenExpired(String token) {
        Date expiration = parseToken(token).getExpiration();
        return expiration.before(new Date());
    }

    // Метод для проверки валидности токена
    public boolean validateToken(String token) {
        try {
            // Попытка парсинга токена (автоматически проверит подпись и срок действия)
            parseToken(token);
            return true;
        } catch (IllegalArgumentException | JwtException e) {
            return false;
        }
    }
}
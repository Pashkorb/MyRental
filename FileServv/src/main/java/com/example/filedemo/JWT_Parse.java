package com.example.filedemo;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.UUID;

public class JWT_Parse {
    private static final String secretKey = "F+pGPpO3Mk3Wf4Q76L6CtPa2tWJYHDJrVZlqTySuYBUTGwoKkyc4ofn/3rlwaPVg";/// будет вынесен вместе с конфигурацией


    public static UUID getUserId(String authorizationHeader) {
        String token = authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : authorizationHeader;

        // Получаем userId из токена
        UUID userId = UUID.fromString(parseToken(token).get("userId", String.class));
        return userId;
    }



    private static Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static Claims parseToken(String token) {
        System.out.println("Проверяю "); // Выводим имя пользователя в консоль

        try {
            // Используем parserBuilder вместо parser
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())  // Устанавливаем ключ
                    .build();

            // Парсим токен и получаем его тело (claims)
            Jws<Claims> claimsJws = parser.parseClaimsJws(token);
            return claimsJws.getBody();  // Возвращаем тело
        } catch (JwtException e) {
            // Обработка ошибок (например, если токен поврежден или просрочен)
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
}
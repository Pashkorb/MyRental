package com.example.filedemo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class TempLinkService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long expirationTime=3600000; // 1 час по умолчанию

    public String generateTempLink(UUID userId, String fileName) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim("userId", userId.toString())
                .claim("fileName", fileName)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public TempFileData validateTempLink(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            Claims claims = jws.getBody();
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                return null;
            }

            String userIdStr = claims.get("userId", String.class);
            String fileName = claims.get("fileName", String.class);

            return new TempFileData(fileName, UUID.fromString(userIdStr));
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public static class TempFileData {
        private final String fileName;
        private final UUID userId;

        public TempFileData(String fileName, UUID userId) {
            this.fileName = fileName;
            this.userId = userId;
        }

        public String getFileName() {
            return fileName;
        }

        public UUID getUserId() {
            return userId;
        }
    }
}
package com.example.Security.service;

import com.example.Security.model.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
@NoArgsConstructor
@Service
public class JWTService {

    @Value("${jwt.secret}")
    public String secretKey;

    // Генерация access токена (15 минут)
    public String generateAccessToken(String username, UUID userId) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 15L * 60 * 10000)) // 15 минут
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Генерация refresh токена (5 недель)
    public String generateRefreshToken(String username, UUID userId) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 5L * 7 * 24 * 60 * 60 * 1000)) // 5 недель
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Role extractRole(String token) {
        Claims claims = extractAllClaims(token);
        String role = claims.get("role", String.class); // Достаем роль из claims
        return Role.valueOf(role); // Преобразуем строку в enum Role
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();

    }
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            System.out.println("Validating JWT: " + token);
            final String username = extractUserName(token);
            boolean isTokenExpired = isTokenExpired(token);

            System.out.println("Username from token: " + username);
            System.out.println("Token expired: " + isTokenExpired);

            return (username.equals(userDetails.getUsername()) && !isTokenExpired);
        } catch (ExpiredJwtException ex) {
            // Обработка истекшего токена
            System.err.println("Token expired: " + ex.getMessage());
            return false;
        } catch (SignatureException ex) {
            // Обработка неверной подписи токена
            System.err.println("Invalid JWT signature: " + ex.getMessage());
            return false;
        } catch (MalformedJwtException ex) {
            // Обработка некорректного формата токена
            System.err.println("Invalid JWT token: " + ex.getMessage());
            return false;
        } catch (UnsupportedJwtException ex) {
            // Обработка неподдерживаемого токена
            System.err.println("Unsupported JWT token: " + ex.getMessage());
            return false;
        } catch (IllegalArgumentException ex) {
            // Обработка пустого или некорректного токена
            System.err.println("JWT claims string is empty: " + ex.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return UUID.fromString(claims.get("userId", String.class)); // Достаем userId из claims
    }
}

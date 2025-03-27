package com.example.rental.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;

@Component
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    public String secret;

    @Value("${jwt.expiration}")
    public long expiration;

    public String generateToken(UUID userId) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .claim("userId", userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 15L * 60 * 10000)) // 15 минут
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public UUID getUserIdFromToken(String token) {
        Object userId = getClaimFromToken(token, claims -> claims.get("userId"));
        if (userId == null) {
            logger.warn("Token userId is null");
            return null;
        }
        try {
            UUID uuid = UUID.fromString(userId.toString());
            logger.debug("Extracted user ID {} from token", uuid);
            return uuid;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format in token userId", e);
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (ExpiredJwtException ex) {
            logger.warn("Token has expired: {}", ex.getMessage());
            return false;
        } catch (Exception ex) {
            logger.error("Token validation failed: {}", ex.getMessage());
            return false;
        }
    }
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        boolean isExpired = expiration.before(new Date());
        logger.debug("Token expiration check: {}", isExpired);
        return isExpired;
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
}
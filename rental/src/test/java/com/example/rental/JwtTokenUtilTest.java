package com.example.rental;

import com.example.rental.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JwtTokenUtilTest {
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.secret}")
    public String secretKey;

    @Value("${jwt.expiration}")
    public long expiration;

    @BeforeEach
    public void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        jwtTokenUtil.secret = secretKey; // Имитация секретного ключа
        jwtTokenUtil.expiration = expiration; // Время жизни токена (в миллисекундах)
    }

    @Test
    public void testGenerateToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtTokenUtil.generateToken(userId);
        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    public void testGetUserIdFromToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtTokenUtil.generateToken(userId);
        UUID extractedUserId = jwtTokenUtil.getUserIdFromToken(token);
        assertEquals(userId, extractedUserId);
    }

    @Test
    public void testValidateToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtTokenUtil.generateToken(userId);
        assertTrue(jwtTokenUtil.validateToken(token));

    }

}

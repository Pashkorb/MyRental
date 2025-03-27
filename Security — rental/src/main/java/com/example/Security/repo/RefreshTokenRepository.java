package com.example.Security.repo;

import com.example.Security.model.RefreshToken;
import com.example.Security.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(Users user);

    Optional<RefreshToken> findByUser(Users user);
}

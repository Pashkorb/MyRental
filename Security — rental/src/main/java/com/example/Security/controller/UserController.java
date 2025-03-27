package com.example.Security.controller;

import com.example.Security.model.*;
import com.example.Security.model.dto.UserDTO;
import com.example.Security.model.enums.Role;
import com.example.Security.service.JWTService;
import com.example.Security.service.MyUserDetailsService;
import com.example.Security.service.RefreshTokenService;
import com.example.Security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/security")
public class UserController{
    @Autowired
    private MyUserDetailsService myUserDetailsService; // Добавляем это
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    JWTService jwtService;
    @Autowired
    public UserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            Users user = new Users();
            user.setLogin(userDTO.getLogin());
            user.setPassword(userDTO.getPassword());
            user.setEmail(userDTO.getEmail());
            // Регистрируем пользователя и получаем токены
            String token = userService.register(user);

            // Возвращаем токены в JSON
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestLoginPassword userDTO) {
        try {
            String token = userService.verify(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String accessToken = authHeader.substring(7);
            String username = jwtService.extractUserName(accessToken);

            Users user = userService.findByLogin(username);

            RefreshToken refreshToken = refreshTokenService.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Refresh token not found"));

            refreshTokenService.verifyExpiration(refreshToken);

            String newAccessToken = jwtService.generateAccessToken(
                    user.getLogin(),
                    user.getId()
            );

            return ResponseEntity.status(HttpStatus.OK).body(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            userService.sendPasswordResetEmail(email);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Password reset email sent"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(request.token(), request.newPassword(), request.email());
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Password reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String accessToken = authHeader.substring(7);
            String username = jwtService.extractUserName(accessToken);

            Users user = userService.findByLogin(username);

            RefreshToken refreshToken = refreshTokenService.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Refresh token not found"));

            refreshTokenService.deleteRefreshToken(refreshToken);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Logout failed: " + e.getMessage()));
        }
    }

}


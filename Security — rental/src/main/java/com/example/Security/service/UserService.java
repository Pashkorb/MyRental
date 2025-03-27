package com.example.Security.service;

import com.example.Security.fieng.RentalFiegn;
import com.example.Security.model.RefreshToken;
import com.example.Security.model.UserRequestLoginPassword;
import com.example.Security.model.dto.UserDataRequest;
import com.example.Security.model.enums.Role;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.Security.model.Users;
import com.example.Security.repo.UserRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    EmailService emailService;

    @Autowired
    private UserRepo repo;

    @Autowired
    RentalFiegn rentalFiegn;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    RedisService redisService;

    @Transactional
    public String register(Users user) {
        if (repo.findByLogin(user.getLogin()).orElse(null) != null) {
            throw new RuntimeException("User already exists");
        }
        if (repo.findByEmail(user.getEmail()).orElse(null) != null) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);

        log.info("Сохранён пользователь: {}", user);
        UUID id = user.getId();
        log.info("Айди пользователя: {}", id);

        String accessToken = jwtService.generateAccessToken(user.getLogin(), id);
        log.info("Сгенерирован access токен: {}", accessToken);

//        // Вызов Feign-клиента с проверкой статуса

        log.info("Вызов Feign-клиента ...");
        ResponseEntity<HttpResponse> response = rentalFiegn.createUser("Bearer " + accessToken,new UserDataRequest(user.getId(),user.getEmail()));
        if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
            throw new RuntimeException("Failed to create user");
        }


        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        log.info("Сгенерирован refresh токен: {}", refreshToken);

        return accessToken;
    }


    public void sendPasswordResetEmail(String email) {
        log.info("User with {email}",email);
        Users user = repo.findByEmail(email).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        int min = 1000; // Минимальное четырехзначное число
        int max = 9999; // Максимальное четырехзначное число
        int token = new Random().nextInt(max - min + 1) + min;

        redisService.saveResetCode(email,token,15);

        // Отправка письма
        emailService.sendPasswordResetEmail(email, token);
    }

    public void resetPassword(int token, String newPassword, String email) {
        // 1. Получаем сохраненный токен из Redis по email
        Integer storedToken = redisService.getResetCode(email);

        // 2. Проверяем, что токены совпадают
        if (storedToken == null || !storedToken.equals(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        // 3. Находим пользователя по email
        Users user = repo.findByEmail(email).orElse(null);
        if (user == null) {
            throw new RuntimeException("Password reset email sent (if user exists)");
        }

        // 4. Обновляем пароль
        user.setPassword(encoder.encode(newPassword));
        repo.save(user);
    }


    public Users findByLogin(String username) {
        return repo.findByLogin(username).orElse(null);
    }

    // Унифицированный метод вместо двух одинаковых
    private String authenticateUser(String username, String password) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        if (authentication.isAuthenticated()) {
            Users user = repo.findByLoginOrEmail(username, username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = jwtService.generateAccessToken(
                    user.getLogin(),
                    user.getId());
            refreshTokenService.createRefreshToken(user);
            return accessToken;
        }
        throw new AuthenticationCredentialsNotFoundException("Invalid credentials");
    }
    public String verify(UserRequestLoginPassword userDTO) {
        // Определяем тип идентификатора
        if (isEmail(userDTO.getIdentifier())) {
            return authenticateByEmail(userDTO.getIdentifier(), userDTO.getPassword());
        } else {
            return authenticateByUsername(userDTO.getIdentifier(), userDTO.getPassword());
        }
    }

    private boolean isEmail(String identifier) {
        return identifier.contains("@");
    }

    private String authenticateByEmail(String email, String password) {
        Users user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return authenticateUser(user.getLogin(), password);
    }

    private String authenticateByUsername(String username, String password) {
        return authenticateUser(username, password);
    }
}

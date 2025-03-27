package com.example.Security.service;

import com.example.Security.model.Users;
import com.example.Security.repo.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getAttribute("id");

        // Ищем пользователя в базе
        Users user = userRepo.findByLogin(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Генерируем JWT
        String accessToken = jwtService.generateAccessToken(user.getLogin(), user.getId());

        // Отправляем JSON-ответ
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + accessToken + "\"}");
    }
}

package com.example.Security.service;

import com.example.Security.model.MyOAuth2User;
import com.example.Security.model.RefreshToken;
import com.example.Security.model.Users;
import com.example.Security.model.enums.Role;
import com.example.Security.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RestTemplate restTemplate; // Для выполнения HTTP-запросов
    @Autowired
    RefreshTokenService refreshTokenService;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("Start OAuth2");

        // Получаем информацию о пользователе от провайдера
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Извлечение токена доступа
        String accessToken = userRequest.getAccessToken().getTokenValue();
        System.out.println(accessToken);

        // Установка заголовков для запроса к API Яндекса
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "OAuth " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Выполнение запроса к API Яндекса
        String userInfoEndpointUri = "https://login.yandex.ru/info?format=json";
        try {
            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map<String, Object> userInfo = response.getBody();

            // Обработка информации о пользователе
            String username = (String) userInfo.get("login"); // Получаем логин пользователя
            String userId = (String) userInfo.get("id"); // Получаем уникальный идентификатор пользователя
            System.out.println(username + userId);

            Optional<Users> usersOptional = userRepo.findByLogin(userId);
            Users user=usersOptional.orElse(null);
            if (user == null) {
                // Если пользователь не найден, создаем нового
                user = new Users();
                user.setLogin(userId);
                user.setPassword(null); // оставляем пустым или null
                user.setOauth2(Boolean.TRUE);
                userRepo.save(user);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

                System.out.println("New user created: " + username + " (ID: " + userId + ")");
            } else {
                System.out.println("User found: " + username + " (ID: " + userId + ")");
            }


            return new MyOAuth2User(user, null, oAuth2User.getAttributes());
        } catch (Exception e) {
            System.out.println("Error while accessing Yandex API: " + e.getMessage());
            OAuth2Error error = new OAuth2Error("invalid_token", "Error retrieving user info from Yandex", null);
            throw new OAuth2AuthenticationException(error, e.getMessage());
        }
    }
}

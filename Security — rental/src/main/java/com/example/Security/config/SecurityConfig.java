package com.example.Security.config;

import com.example.Security.service.CustomOAuth2UserService;
import com.example.Security.service.JWTService;
import com.example.Security.service.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    @Autowired
    private JWTService jwtService;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    OAuth2LoginSuccessHandler successHandler;
    @Autowired
    private UserDetailsService userDetailsService;
    public SecurityConfig(JWTService jwtService) {
        this.jwtService = jwtService;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Разрешаем OPTIONS-запросы
                        .requestMatchers(
                                "/**").permitAll()
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(successHandler)
                        .authorizationEndpoint(authEndpoint -> authEndpoint
                                .baseUri("/api/security/oauth2/authorization") // Кастомный путь
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )


                )
                .httpBasic(Customizer.withDefaults()) // Оставляем базовую аутентификацию для дебага
                .build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
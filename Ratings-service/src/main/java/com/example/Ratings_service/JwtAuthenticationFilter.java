package com.example.Ratings_service;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtTokenUtil.extractTokenFromCookie(request);
        if (token != null) {
            logger.debug("Extracted token: {}", token);
            if (jwtTokenUtil.validateToken(token)) {
                UUID userId = jwtTokenUtil.getUserIdFromToken(token);
                logger.info("Token is valid for user ID: {}", userId);

                if (userId != null) {
                    // Создаем UserPrincipal

                    // Устанавливаем аутентификацию в SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.warn("UserId not found", userId);
                }


            } else {
                logger.warn("Token is invalid or expired");
            }
        } else {
            logger.debug("No token found in cookies");
        }

        filterChain.doFilter(request, response);
    }


}
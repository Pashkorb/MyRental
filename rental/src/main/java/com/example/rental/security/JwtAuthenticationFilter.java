package com.example.rental.security;

import com.example.rental.users.User;
import com.example.rental.users.UserRepo;
import com.example.rental.users.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    UserRepo userRepo;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader=request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token=authHeader.substring(7);
            logger.debug("Extracted token from header: {}", token);
        }

        if (token != null) {
            logger.debug("Extracted token: {}", token);
            if (jwtTokenUtil.validateToken(token)) {
                UUID userId = jwtTokenUtil.getUserIdFromToken(token);
                logger.info("Token is valid for user ID: {}", userId);

                // Получаем пользователя по UUID
                User user = userRepo.findByUuid(userId);
                if (user != null) {
                    // Создаем UserPrincipal
                    UserPrincipal principal = new UserPrincipal(user.getUuid(),token);
                    // Устанавливаем аутентификацию в SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.warn("User not found for UUID: {}", userId);
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
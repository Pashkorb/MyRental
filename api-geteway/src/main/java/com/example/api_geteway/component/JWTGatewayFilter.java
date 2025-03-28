package com.example.api_geteway.component;

import com.example.api_geteway.service.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JWTGatewayFilter extends AbstractGatewayFilterFactory<JWTGatewayFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(JWTGatewayFilter.class);

    @Autowired
    private JWTService jwtService;

    public JWTGatewayFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }

            String requestPath = exchange.getRequest().getURI().getPath();

            logger.debug("=== Обработка запроса началась ===");
            logger.debug("Проверка наличия токена...");

            if (requestPath.startsWith("/test")
                    || requestPath.startsWith("/cors")
                    || requestPath.startsWith("/api/security")) {
                logger.debug("CORS или тестовый запрос, пропускаем проверку токена");
                return chain.filter(exchange);
            }

            String token = extractToken(exchange);

            if (token == null) {
                logger.warn("Токен отсутствует. Перенаправление на логин...");
                return handleUnauthorized(exchange);
            }

            logger.debug("Токен найден (длина: {})", token.length());
            logger.debug("Проверка валидности токена...");

            if (!jwtService.validateToken(token)) {
                logger.warn("Токен невалиден. Перенаправление на логин...");
                return handleUnauthorized(exchange);
            }

            logger.debug("Токен валиден. Передача управления следующему фильтру.");
            return chain.filter(exchange);
        };
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.debug("Токен найден в заголовке Authorization");
            return authHeader.substring(7);
        }

        if (exchange.getRequest().getCookies().getFirst("JWT") != null) {
            logger.debug("Токен найден в cookies");
            return exchange.getRequest().getCookies().getFirst("JWT").getValue();
        }

        logger.debug("Токен отсутствует в заголовках и cookies");
        return null;
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        logger.warn("Неавторизованный доступ. Возврат статуса 401 Unauthorized");
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}
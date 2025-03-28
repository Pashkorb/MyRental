//package com.example.api_geteway.config;
//
//import com.example.api_geteway.service.JWTService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//public class JWTGatewayFilter implements GatewayFilter {
//
//    @Autowired
//    private JWTService jwtService;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        // Извлекаем токен из заголовка
//        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//        String token = null;
//        String username = null;
//
//        System.out.println("=== Начало обработки запроса ===");
//        System.out.println("Проверка заголовка Authorization...");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            System.out.println("Токен найден в заголовке: " + token);
//        } else {
//            System.out.println("Токен в заголовке отсутствует.");
//        }
//
//        // Если токена нет в заголовке, ищем его в cookies
//        if (token == null) {
//            System.out.println("Проверка cookies на наличие токена...");
//            token = exchange.getRequest().getCookies().getFirst("JWT") != null ?
//                    exchange.getRequest().getCookies().getFirst("JWT").getValue() : null;
//
//            if (token != null) {
//                System.out.println("Токен найден в cookies: " + token);
//            } else {
//                System.out.println("Токен отсутствует в cookies.");
//            }
//        }
//
//        // Если токен отсутствует, перенаправляем на /login
//        if (token == null) {
//            System.out.println("Токен отсутствует. Перенаправление на login...");
//            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FOUND);
//            exchange.getResponse().getHeaders().setLocation(java.net.URI.create("http://localhost:8766/security-service/login"));
//            return exchange.getResponse().setComplete();
//        }
//
//        try {
//            System.out.println("Извлечение имени пользователя из токена...");
//            username = jwtService.extractUserName(token);
//            System.out.println("Имя пользователя из токена: " + username);
//
//            // Валидируем токен
//            System.out.println("Проверка валидности токена...");
//            if (username != null && jwtService.validateToken(token, null)) {
//                System.out.println("Токен валиден для пользователя: " + username);
//                // Создаем аутентификацию (если потребуется в будущем)
//                // Здесь можно работать с контекстом безопасности, если это нужно для ваших целей.
//            } else {
//                System.out.println("Токен невалиден. Перенаправление на login...");
//                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FOUND);
//                exchange.getResponse().getHeaders().setLocation(java.net.URI.create("http://localhost:8766/security-service/login"));
//                return exchange.getResponse().setComplete();
//            }
//        } catch (Exception e) {
//            System.out.println("Ошибка при обработке токена: " + e.getMessage());
//            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FOUND);
//            exchange.getResponse().getHeaders().setLocation(java.net.URI.create("http://localhost:8766/security-service/login"));
//            return exchange.getResponse().setComplete();
//        }
//
//        System.out.println("Обработка запроса завершена. Передача управления следующему фильтру.");
//        return chain.filter(exchange); // Передаем управление дальше по цепочке
//    }
//}

//package com.example.api_geteway.config;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//
//public class GatewayConfig {
//    private final JWTGatewayFilter jwtGatewayFilter;
//
//    public GatewayConfig(JWTGatewayFilter jwtGatewayFilter) {
//        this.jwtGatewayFilter = jwtGatewayFilter;
//    }
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("secure_route", r -> r.path("/secure/**")
//                        .filters(f -> f.filter(jwtGatewayFilter)) // Применяем фильтр вручную
//                        .uri("lb://your-microservice")) // указываем адрес микросервиса
//                .build();
//    }
//}

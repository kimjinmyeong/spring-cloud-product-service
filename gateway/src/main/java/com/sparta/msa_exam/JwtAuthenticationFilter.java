package com.sparta.msa_exam;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.info("Received request for path: {}", path);

        // Allow requests to /auth/** without authentication
        if (path.startsWith("/auth")) {
            log.info("Path {} is allowed without authentication", path);
            return chain.filter(exchange);
        }

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));

        // Extract Token
        String authorizationHeader = request.getHeaders().getFirst("Authorization");
        if (!extractToken(authorizationHeader)) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Validate Token
        if (!validateToken(authorizationHeader, key)) {
            log.warn("Token validation failed for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        log.info("Token validated successfully for path: {}", path);
        return chain.filter(exchange);
    }

    private boolean extractToken(String authorizationHeader) {
        boolean valid = authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
        if (valid) {
            log.debug("Extracted token from Authorization header");
        } else {
            log.debug("Failed to extract token from Authorization header");
        }
        return valid;
    }

    private boolean validateToken(String authorizationHeader, SecretKey key) {
        try {
            String token = authorizationHeader.substring(7);
            Jws<Claims> jwsClaims = Jwts.parser()
                    .verifyWith(key)
                    .build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Exception during token validation: {}", e.getMessage(), e);
            return false;
        }
    }
}
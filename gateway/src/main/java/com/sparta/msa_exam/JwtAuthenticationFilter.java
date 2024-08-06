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

        // Allow requests to /auth/** without authentication
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/auth")) {
            return chain.filter(exchange);
        }

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));

        // Extract Token.
        String authorizationHeader = request.getHeaders().getFirst("Authorization");
        if (!extractToken(authorizationHeader)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Validate Token.
        if (!validateToken(exchange, authorizationHeader, key)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    private boolean extractToken(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }

    private boolean validateToken(ServerWebExchange exchange, String authorizationHeader, SecretKey key) {
        try {
            String token = authorizationHeader.substring(7);
            Jws<Claims> jwsClaims = Jwts.parser()
                    .verifyWith(key)
                    .build().parseSignedClaims(token);

            Claims claims = jwsClaims.getPayload();
            exchange.getRequest().mutate()
                    .header("X-User-Id", claims.get("user_id").toString())
                    .header("X-Role", claims.get("role").toString())
                    .build();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}


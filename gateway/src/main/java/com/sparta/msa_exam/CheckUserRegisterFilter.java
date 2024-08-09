package com.sparta.msa_exam;

import com.sparta.msa_exam.client.AuthClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class CheckUserRegisterFilter implements GlobalFilter, Ordered {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    private final AuthClient authClient;

    public CheckUserRegisterFilter(AuthClient authClient) {
        this.authClient = authClient;
    }

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

        String userId = getUserIdFromToken(exchange);
        return authClient.validateUserExists(userId)
                .flatMap(status -> {
                    if (status == HttpStatus.OK) {
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                });
    }

    private String getUserIdFromToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String token = authHeader.substring(7);
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        Jws<Claims> jwsClaims = Jwts.parser()
                .verifyWith(key)
                .build().parseSignedClaims(token);
        String userId = jwsClaims.getPayload().get("user_id").toString();
        return userId;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }
}

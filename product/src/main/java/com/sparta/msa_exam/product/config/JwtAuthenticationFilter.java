package com.sparta.msa_exam.product.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Extract Token
        String authorizationHeader = request.getHeader("Authorization");
        if (!extractToken(authorizationHeader)) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));

        // Validate Token
        if (!validateToken(authorizationHeader, key)) {
            log.warn("Token validation failed for path: {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
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
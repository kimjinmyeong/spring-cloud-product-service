package com.sparta.msa_exam.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;

@Slf4j
public class JwtUtil {

    public static boolean extractToken(String authorizationHeader) {
        boolean valid = authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
        if (valid) {
            log.debug("Extracted token from Authorization header");
        } else {
            log.debug("Failed to extract token from Authorization header");
        }
        return valid;
    }

    public static boolean validateToken(String authorizationHeader, SecretKey key) {
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

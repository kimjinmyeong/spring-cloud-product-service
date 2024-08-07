package com.sparta.msa_exam.service.impl;

import com.sparta.msa_exam.service.AuthService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${service.jwt.secret-key}")
    private SecretKey secretKey;

    @Override
    public String createAccessToken(String userId) {
        return Jwts.builder()
                .claim("user_id", userId)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }
}
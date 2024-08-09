package com.sparta.msa_exam.service.impl;

import com.sparta.msa_exam.dto.RegisterRequestDto;
import com.sparta.msa_exam.entity.User;
import com.sparta.msa_exam.repository.UserRepository;
import com.sparta.msa_exam.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${service.jwt.secret-key}")
    private String key;

    private final UserRepository userRepository;

    @Override
    public String signIn(String userId) {
        if (checkUserExists(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A user with this username not exists.");
        }
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(key));
        return Jwts.builder()
                .claim("user_id", userId)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    @Override
    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        String userId = registerRequestDto.getUserId();
        log.info("register");
        if (!checkUserExists(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with this username already exists.");
        }

        User user = new User(userId);
        userRepository.save(user);
    }

    @Override
    public boolean checkUserExists(String userId) {
        return userRepository.findByUserId(userId).isEmpty();
    }
}

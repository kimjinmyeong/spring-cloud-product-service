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
        log.debug("Entering signIn method with userId: {}", userId);

        if (checkUserExists(userId)) {
            log.warn("User with userId: {} not found.", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A user with this username does not exist.");
        }

        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(key));
        String token = Jwts.builder()
                .claim("user_id", userId)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        log.debug("JWT token generated for userId: {}", userId);
        return token;
    }

    @Override
    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        String userId = registerRequestDto.getUserId();
        log.debug("Entering register method with userId: {}", userId);

        if (!checkUserExists(userId)) {
            log.warn("User with userId: {} already exists.", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with this username already exists.");
        }

        User user = new User(userId);
        userRepository.save(user);

        log.info("User with userId: {} successfully registered.", userId);
    }

    @Override
    public boolean checkUserExists(String userId) {
        log.debug("Checking existence of user with userId: {}", userId);
        return userRepository.findByUserId(userId).isEmpty();
    }
}
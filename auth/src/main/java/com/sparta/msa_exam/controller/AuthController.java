package com.sparta.msa_exam.controller;

import com.sparta.msa_exam.dto.AuthResponseDto;
import com.sparta.msa_exam.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/signIn")
    public ResponseEntity<?> createAuthenticationToken(@RequestParam(name = "user_id") String userId){
        return ResponseEntity.ok(new AuthResponseDto(authService.createAccessToken(userId)));
    }
}
package com.sparta.msa_exam.controller;

import com.sparta.msa_exam.dto.AuthResponseDto;
import com.sparta.msa_exam.dto.RegisterRequestDto;
import com.sparta.msa_exam.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/signIn")
    public ResponseEntity<AuthResponseDto> signIn(@RequestParam(name = "user_id") String userId){
        return ResponseEntity.ok(new AuthResponseDto(authService.signIn(userId)));
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequestDto){
        authService.register(registerRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateUserExists(@Valid @RequestParam String userId){
        if (authService.checkUserExists(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

}
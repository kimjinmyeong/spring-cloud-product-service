package com.sparta.msa_exam.service;

import com.sparta.msa_exam.dto.RegisterRequestDto;

public interface AuthService {
    String signIn(String userId);

    void register(RegisterRequestDto registerRequestDto);

    boolean checkUserExists(String userId);
}

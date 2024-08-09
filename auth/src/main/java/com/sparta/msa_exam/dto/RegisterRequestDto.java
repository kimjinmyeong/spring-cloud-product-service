package com.sparta.msa_exam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RegisterRequestDto {
    @NotBlank
    private String userId;
}

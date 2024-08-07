package com.sparta.msa_exam.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequestDto {
    private String orderName;
    private List<Long> productIds;
}

package com.sparta.msa_exam.dto;

import lombok.Getter;

@Getter
public class ProductDto {
    private Long productId;
    private String productName;
    private Integer supplyPrice;
}
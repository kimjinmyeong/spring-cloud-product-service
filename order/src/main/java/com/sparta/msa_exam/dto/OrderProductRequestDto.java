package com.sparta.msa_exam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OrderProductRequestDto {
    @JsonProperty("product_id")
    private Long productId;
}

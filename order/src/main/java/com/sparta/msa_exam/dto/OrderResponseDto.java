package com.sparta.msa_exam.dto;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class OrderResponseDto implements Serializable {
    private Long orderId;
    private List<Integer> productIds;

    public OrderResponseDto(Long orderId, List<Integer> productIds) {
        this.orderId = orderId;
        this.productIds = productIds;
    }
}

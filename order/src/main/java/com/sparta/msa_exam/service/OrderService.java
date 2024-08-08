package com.sparta.msa_exam.service;

import com.sparta.msa_exam.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {
    String createOrder(String token, String name, List<Long> productIds);
    String addProductToOrder(String token, Long orderId, Long productId);
    OrderResponseDto getOrderById(Long orderId);
}


package com.sparta.msa_exam.service;

import com.sparta.msa_exam.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {
    void createOrder(String name, List<Long> productIds);
    void addProductToOrder(Long orderId, Long productId);
    OrderResponseDto getOrderById(Long orderId);
}


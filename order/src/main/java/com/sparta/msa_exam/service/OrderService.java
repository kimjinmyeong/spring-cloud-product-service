package com.sparta.msa_exam.service;

import com.sparta.msa_exam.dto.OrderResponseDto;
import com.sparta.msa_exam.dto.ProductDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(List<ProductDto> products, String token, String name, List<Long> productIds);
    OrderResponseDto addProductToOrder(List<ProductDto> products, String token, Long orderId, Long productId);
    OrderResponseDto getOrderById(Long orderId);
}


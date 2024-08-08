package com.sparta.msa_exam.controller;

import com.sparta.msa_exam.dto.OrderProductRequestDto;
import com.sparta.msa_exam.dto.OrderRequestDto;
import com.sparta.msa_exam.dto.OrderResponseDto;
import com.sparta.msa_exam.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(HttpServletRequest request, @RequestBody OrderRequestDto orderRequestDto) {
        String token = request.getHeader("Authorization");
        String clientPort = orderService.createOrder(token, orderRequestDto.getOrderName(), orderRequestDto.getProductIds());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).header("Server-Port", clientPort).build();
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Void> addProductToOrder(HttpServletRequest request, @PathVariable Long orderId, @RequestBody OrderProductRequestDto requestDto) {
        String token = request.getHeader("Authorization");
        String clientPort = orderService.addProductToOrder(token, orderId, requestDto.getProductId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).header("Server-Port", clientPort).build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long orderId) {
        OrderResponseDto orderResponseDto = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderResponseDto);
    }

}

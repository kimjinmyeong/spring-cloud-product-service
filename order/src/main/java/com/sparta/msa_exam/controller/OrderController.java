package com.sparta.msa_exam.controller;

import com.sparta.msa_exam.client.ProductClient;
import com.sparta.msa_exam.dto.OrderProductRequestDto;
import com.sparta.msa_exam.dto.OrderRequestDto;
import com.sparta.msa_exam.dto.OrderResponseDto;
import com.sparta.msa_exam.dto.ProductDto;
import com.sparta.msa_exam.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final ProductClient productClient;

    @PostMapping
    public ResponseEntity<Long> createOrder(HttpServletRequest request, @RequestBody OrderRequestDto orderRequestDto) {
        String token = request.getHeader("Authorization");
        ResponseEntity<List<ProductDto>> clientResponseEntity = productClient.getProducts(token);
        List<ProductDto> products = clientResponseEntity.getBody();
        orderService.createOrder(products, token, orderRequestDto.getOrderName(), orderRequestDto.getProductIds());

        String clientPort = clientResponseEntity.getHeaders().get("Server-Port").get(0);
        log.debug("Received Server-Port by product server: {}", clientPort);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).header("Server-Port", clientPort).build();
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Void> addProductToOrder(HttpServletRequest request, @PathVariable Long orderId, @RequestBody OrderProductRequestDto requestDto) {
        String token = request.getHeader("Authorization");
        ResponseEntity<List<ProductDto>> clientResponseEntity = productClient.getProducts(token);
        List<ProductDto> products = clientResponseEntity.getBody();
        orderService.addProductToOrder(products, token, orderId, requestDto.getProductId());

        String clientPort = clientResponseEntity.getHeaders().get("Server-Port").get(0);
        log.debug("Received Server-Port by product server: {}", clientPort);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).header("Server-Port", clientPort).build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long orderId) {
        OrderResponseDto orderResponseDto = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderResponseDto);
    }

}

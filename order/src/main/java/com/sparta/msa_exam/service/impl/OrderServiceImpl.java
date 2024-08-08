package com.sparta.msa_exam.service.impl;

import com.sparta.msa_exam.client.ProductClient;
import com.sparta.msa_exam.dto.OrderResponseDto;
import com.sparta.msa_exam.dto.ProductDto;
import com.sparta.msa_exam.entity.Order;
import com.sparta.msa_exam.entity.OrderProduct;
import com.sparta.msa_exam.repository.OrderProductRepository;
import com.sparta.msa_exam.repository.OrderRepository;
import com.sparta.msa_exam.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    @Transactional
    @Override
    public String createOrder(String token, String orderName, List<Long> productRequestIds) {
        log.info("Creating order with name: {} and products: {}", orderName, productRequestIds);

        ResponseEntity<List<ProductDto>> clientResponseEntity = productClient.getProducts(token);
        String clientPort = clientResponseEntity.getHeaders().get("Server-Port").get(0);

        List<ProductDto> productDtoList = clientResponseEntity.getBody();
        log.debug("Retrieved {} products from product client.", productDtoList.size());
        validateProductDtoList(productRequestIds, productDtoList);

        Order order = Order.builder()
                .orderName(orderName)
                .build();
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getOrderId());

        List<OrderProduct> orderProductList = new ArrayList<>();
        for (Long productRequestId : productRequestIds) {
            OrderProduct orderProduct = OrderProduct.builder()
                    .order(savedOrder)
                    .productId(productRequestId)
                    .build();
            orderProductList.add(orderProduct);
        }
        List<OrderProduct> savedOrderProductList = orderProductRepository.saveAll(orderProductList);

        savedOrder.setProducts(savedOrderProductList);
        orderRepository.save(savedOrder);
        log.info("Order updated with products. Total products in order: {}", savedOrder.getProducts().size());

        return clientPort;
    }

    @Transactional
    @Override
    public String addProductToOrder(String token, Long orderId, Long productId) {
        log.info("Adding product with ID: {} to order ID: {}", productId, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order not found"));
        log.debug("Order found with ID: {}. Current products: {}", orderId, order.getProducts());

        ResponseEntity<List<ProductDto>> clientResponseEntity = productClient.getProducts(token);
        String clientPort = clientResponseEntity.getHeaders().get("Server-Port").get(0);

        List<ProductDto> productDtoList = clientResponseEntity.getBody();
        Set<Long> productIdSet = productDtoList.stream()
                .map(ProductDto::getProductId)
                .collect(Collectors.toSet());
        log.debug("Available products from product client: {}", productIdSet);

        if (!productIdSet.contains(productId)) {
            log.error("Product ID: {} not found in available products", productId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found");
        }

        order.getProducts().add(OrderProduct.builder()
                .productId(productId)
                .order(order)
                .build());
        orderRepository.save(order);
        log.info("Product ID: {} added to order ID: {}.", productId, orderId);

        return clientPort;
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponseDto getOrderById(Long orderId) {
        log.info("Retrieving order by ID: {}", orderId);

        List<Integer> productIdList = orderProductRepository.findAllProductIdByOrderId(orderId);
        log.debug("Products found for order ID {}: {}", orderId, productIdList);

        return new OrderResponseDto(orderId, productIdList);
    }

    private void validateProductDtoList(List<Long> productRequestIds, List<ProductDto> productDtoList) {
        log.debug("Validating product request IDs: {}", productRequestIds);

        if (productDtoList == null || productDtoList.isEmpty()) {
            log.error("No products found from product client.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find any products");
        }

        Set<Long> productIdSet = productDtoList.stream()
                .map(ProductDto::getProductId)
                .collect(Collectors.toSet());
        for (Long productId : productRequestIds) {
            if (!productIdSet.contains(productId)) {
                log.error("Requested product ID: {} not found in product list {}", productId, productIdSet);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product Id is not in " + productIdSet);
            }
        }
        log.info("All requested product IDs are valid.");
    }
}
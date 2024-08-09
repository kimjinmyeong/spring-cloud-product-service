package com.sparta.msa_exam.service.impl;

import com.sparta.msa_exam.dto.OrderResponseDto;
import com.sparta.msa_exam.dto.ProductDto;
import com.sparta.msa_exam.entity.Order;
import com.sparta.msa_exam.entity.OrderProduct;
import com.sparta.msa_exam.repository.OrderProductRepository;
import com.sparta.msa_exam.repository.OrderRepository;
import com.sparta.msa_exam.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    @Transactional
    @Override
    public OrderResponseDto createOrder(List<ProductDto> products, String token, String orderName, List<Long> productRequestIds) {
        log.info("Creating order with name: {} and products: {}", orderName, productRequestIds);
        log.debug("Retrieved {} products from product client.", products.size());
        validateProductDtoList(productRequestIds, products);

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
        List<OrderProduct> createdOrderProductList = createOrderProduct(savedOrder, productRequestIds);
        savedOrder.setProducts(createdOrderProductList);
        Order updatedOrder = orderRepository.save(savedOrder);
        log.info("Order updated with products. Total products in order: {}", savedOrder.getProducts().size());
        return createOrderResponseDto(updatedOrder);
    }

    @Transactional
    @CachePut(value = "orders", key = "#result.getOrderId()")
    @Override
    public OrderResponseDto addProductToOrder(List<ProductDto> products, String token, Long orderId, Long productId) {
        log.info("Adding product with ID: {} to order ID: {}", productId, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order not found"));
        log.debug("Order found with ID: {}. Current products: {}", orderId, order.getProducts());

        validateProductDtoList(order.getProducts().stream().map(OrderProduct::getProductId).toList(), products);

        order.getProducts().add(OrderProduct.builder()
                .productId(productId)
                .order(order)
                .build());
        Order savedOrder = orderRepository.save(order);
        log.info("Product ID: {} added to order ID: {}.", productId, orderId);
        return createOrderResponseDto(savedOrder);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#orderId")
    @Override
    public OrderResponseDto getOrderById(Long orderId) {
        log.info("Retrieving order by ID: {}", orderId);

        List<Integer> productIdList = orderProductRepository.findAllProductIdByOrderId(orderId);
        if (productIdList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
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

    private List<OrderProduct> createOrderProduct(Order order, List<Long> productIds) {
        List<OrderProduct> orderProductList = new ArrayList<>();
        for (Long productRequestId : productIds) {
            OrderProduct orderProduct = OrderProduct.builder()
                    .order(order)
                    .productId(productRequestId)
                    .build();
            orderProductList.add(orderProduct);
        }
        return orderProductRepository.saveAll(orderProductList);
    }

    private OrderResponseDto createOrderResponseDto (Order order) {
        List<Integer> productIdList = order.getProducts().stream()
                .map(OrderProduct::getProductId)
                .map(Long::intValue)
                .collect(Collectors.toList());
        return new OrderResponseDto(order.getOrderId(), productIdList);
    }
}
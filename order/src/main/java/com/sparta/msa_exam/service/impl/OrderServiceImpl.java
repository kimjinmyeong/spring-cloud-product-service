package com.sparta.msa_exam.service.impl;

import com.sparta.msa_exam.client.ProductClient;
import com.sparta.msa_exam.dto.OrderResponseDto;
import com.sparta.msa_exam.dto.ProductDto;
import com.sparta.msa_exam.entity.Order;
import com.sparta.msa_exam.entity.OrderProduct;
import com.sparta.msa_exam.repository.OrderProductRepository;
import com.sparta.msa_exam.repository.OrderRepository;
import com.sparta.msa_exam.service.OrderService;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductClient productClient;

    private final OrderRepository orderRepository;

    private final OrderProductRepository orderProductRepository;

    @Transactional
    @Override
    public void createOrder(String orderName, List<Long> productRequestIds) {
        List<ProductDto> productDtoList = productClient.getProducts();
        validateProductDtoList(productRequestIds, productDtoList);

        Order order = Order.builder()
                .orderName(orderName)
                .build();
        Order savedOrder = orderRepository.save(order);

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
    }

    @Override
    public void addProductToOrder(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BadRequestException("Order not found"));
        Set<Long> productIdSet = productClient.getProducts().stream()
                .map(ProductDto::getProductId)
                .collect(Collectors.toSet());
        if (!productIdSet.contains(productId)) {
            throw new BadRequestException("Product not found");
        }

        order.getProducts().add(OrderProduct.builder()
                .productId(productId)
                .order(order)
                .build());
        orderRepository.save(order);
    }

    @Override
    public OrderResponseDto getOrderById(Long orderId) {
        List<Integer> productIdList = orderProductRepository.findAllProductIdByOrderId(orderId);
        return new OrderResponseDto(orderId, productIdList);
    }

    private void validateProductDtoList(List<Long> productRequestIds, List<ProductDto> productDtoList) {
        if (productDtoList == null || productDtoList.isEmpty()) {
            throw new BadRequestException("Cannot find any products");
        }

        Set<Long> productIdSet = productDtoList.stream()
                .map(ProductDto::getProductId)
                .collect(Collectors.toSet());
        for (Long productId : productRequestIds) {
            if (!productIdSet.contains(productId)) {
                throw new BadRequestException("Product Id is not in " + productIdSet);
            }
        }
    }

}

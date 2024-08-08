package com.sparta.msa_exam.product.controller;

import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.entity.Product;
import com.sparta.msa_exam.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        log.info("Received request to get all products.");
        List<Product> products = productService.findAll();
        log.info("Returning {} products.", products.size());
        return ResponseEntity.ok().body(products);
    }

    @PostMapping
    public ResponseEntity<Void> addProduct(@RequestBody ProductRequestDto productRequestDto) {
        log.info("Received request to add a new product: {}", productRequestDto);
        productService.addProduct(productRequestDto);
        log.info("Product added successfully.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

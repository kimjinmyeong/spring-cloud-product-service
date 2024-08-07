package com.sparta.msa_exam.product.controller;

import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.entity.Product;
import com.sparta.msa_exam.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok().body(productService.findAll());
    }

    @PostMapping
    public ResponseEntity<Void> addProduct(@RequestBody ProductRequestDto productRequestDto) {
        productService.addProduct(productRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

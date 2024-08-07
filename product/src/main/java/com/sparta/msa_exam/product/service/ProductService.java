package com.sparta.msa_exam.product.service;

import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();
    void addProduct(ProductRequestDto productRequestDto);
}

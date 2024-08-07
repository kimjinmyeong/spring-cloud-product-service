package com.sparta.msa_exam.product.service.impl;

import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.entity.Product;
import com.sparta.msa_exam.product.repository.ProductRepository;
import com.sparta.msa_exam.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional
    @Override
    public void addProduct(ProductRequestDto productRequestDto) {
        Product product = new Product(productRequestDto);
        productRepository.save(product);
    }
}

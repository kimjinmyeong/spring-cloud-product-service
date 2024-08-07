package com.sparta.msa_exam.client;

import com.sparta.msa_exam.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "product")
public interface ProductClient {
    @GetMapping("/products")
    List<ProductDto> getProducts();
}

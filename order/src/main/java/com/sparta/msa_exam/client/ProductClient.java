package com.sparta.msa_exam.client;

import com.sparta.msa_exam.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "product")
public interface ProductClient {
    @GetMapping("/products")
    ResponseEntity<List<ProductDto>> getProducts(@RequestHeader("Authorization") String token);
}

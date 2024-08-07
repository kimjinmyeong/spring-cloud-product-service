package com.sparta.msa_exam.product.entity;

import com.sparta.msa_exam.product.dto.ProductRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name")
    private String productName;

    @Column(name = "supply_price")
    private Integer supplyPrice;

    public Product(ProductRequestDto productRequestDto) {
        this.productName = productRequestDto.getProductName();
        this.supplyPrice = productRequestDto.getSupplyPrice();
    }

}

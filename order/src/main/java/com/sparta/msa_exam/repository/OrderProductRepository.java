package com.sparta.msa_exam.repository;

import com.sparta.msa_exam.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query("SELECT op.productId FROM OrderProduct op JOIN op.order o WHERE o.orderId = :orderId")
    List<Integer> findAllProductIdByOrderId(@Param("orderId") Long orderId);

}

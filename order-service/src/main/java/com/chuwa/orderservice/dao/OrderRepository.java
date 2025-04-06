package com.chuwa.orderservice.dao;

import com.chuwa.orderservice.entity.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(Long orderId);

    Page<Order> findOrdersByUserId(UUID userId, Pageable pageable);

    Optional<Order> findOrderByUserIdAndFlashSaleId(UUID userId, Long flashSaleId);
}


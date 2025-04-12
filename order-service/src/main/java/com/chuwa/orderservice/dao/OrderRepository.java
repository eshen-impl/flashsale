package com.chuwa.orderservice.dao;

import com.chuwa.orderservice.entity.Order;

import com.chuwa.orderservice.enums.OrderStatus;
import com.chuwa.orderservice.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(Long orderId);

    Page<Order> findOrdersByUserId(UUID userId, Pageable pageable);

    Optional<Order> findOrderByUserIdAndFlashSaleId(UUID userId, Long flashSaleId);

    Optional<Order> findOrderByOrderIdAndUserId(Long orderId, UUID userId);

    @Modifying
    @Query("UPDATE Order o SET o.orderStatus = :newOrderStatus, " +
            "o.paymentStatus = :newPaymentStatus, " +
            "o.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE o.orderId = :orderId AND o.paymentStatus != :curPaymentStatus")
    int cancelUnpaidOrder(@Param("orderId") Long orderId,
                          @Param("newOrderStatus") OrderStatus newOrderStatus,
                          @Param("newPaymentStatus") PaymentStatus newPaymentStatus,
                          @Param("curPaymentStatus") PaymentStatus curPaymentStatus);

}


package com.chuwa.orderservice.dao;

import com.chuwa.orderservice.entity.Order;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends CassandraRepository<Order, UUID> {
    Optional<Order> findByOrderId(UUID orderId);
}


package com.chuwa.orderservice.dao;

import com.chuwa.orderservice.entity.OrderByUser;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderByUserRepository extends CassandraRepository<OrderByUser, UUID> {
    List<OrderByUser> findByUserId(UUID userId);
}


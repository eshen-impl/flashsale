package com.chuwa.accountservice.dao;

import com.chuwa.accountservice.model.PaymentMethod;
import com.chuwa.accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findPaymentMethodsByUser(User user);

    @Query("SELECT p FROM PaymentMethod p WHERE p.paymentMethodId = :paymentMethodId AND p.user.id = :userId")
    Optional<PaymentMethod> findByPaymentMethodIdAndUserId(@Param("paymentMethodId") Long paymentMethodId, @Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM PaymentMethod p WHERE p.paymentMethodId = :paymentMethodId AND p.user.id = :userId")
    void deleteByPaymentMethodIdAndUserId(@Param("paymentMethodId") Long paymentMethodId, @Param("userId") UUID userId);
}

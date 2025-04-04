package com.chuwa.accountservice.service;

import com.chuwa.accountservice.payload.PaymentMethodDTO;

import java.util.List;
import java.util.UUID;

public interface PaymentMethodService {
    PaymentMethodDTO addPaymentMethod(UUID userId, PaymentMethodDTO paymentMethodDTO);
    List<PaymentMethodDTO> getPaymentMethodsByUserId(UUID userId);
    PaymentMethodDTO updatePaymentMethod(UUID userId, PaymentMethodDTO paymentMethodDTO);
    void removePaymentMethod(UUID userId, Long paymentMethodId);

    PaymentMethodDTO getPaymentMethodByUserIdAndPaymentMethodId(UUID userId, Long paymentMethodId);
}

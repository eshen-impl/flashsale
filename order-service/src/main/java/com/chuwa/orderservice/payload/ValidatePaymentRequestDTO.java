package com.chuwa.orderservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidatePaymentRequestDTO {

    private UUID orderId;
    private UUID transactionKey;
    private Long paymentMethodId;

    private BigDecimal amount;

    private String currency;
}

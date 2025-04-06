package com.chuwa.orderservice.payload;

import com.chuwa.orderservice.enums.PaymentEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private PaymentEventType eventType;
    private UUID userId;
    private Long orderId;

    private BigDecimal refundedAmount;

//    private String paymentCardLast4;
//    private Instant paymentTimestamp;
}


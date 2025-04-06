package com.chuwa.orderservice.payload;

import com.chuwa.orderservice.enums.OrderEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private OrderEventType eventType;
    private UUID userId;
    private Long orderId;

    private String items;

    private String shippingAddress;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

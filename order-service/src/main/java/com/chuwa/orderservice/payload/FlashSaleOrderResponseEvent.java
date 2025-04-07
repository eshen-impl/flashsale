package com.chuwa.orderservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleOrderResponseEvent {
    private enum Status {
        PENDING,
        CONFIRMED,
        FAILED
    }
    private Long orderId;
}

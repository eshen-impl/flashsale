package com.chuwa.orderservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DelayedOrderEntry {
    private Long orderId;
    private Long flashSaleId;
    private UUID userId;
}

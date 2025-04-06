package com.chuwa.orderservice.payload;

import com.chuwa.orderservice.enums.ShippingEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingEvent {
    private ShippingEventType eventType;
    private UUID userId;
    private Long orderId;


}


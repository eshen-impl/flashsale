package com.chuwa.orderservice.payload;

import com.chuwa.orderservice.enums.FlashSaleOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleOrderResponseEvent {
    private FlashSaleOrderStatus status;
    private Long orderId;
    private String message;
    private UUID userId;

}

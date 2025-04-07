package com.chuwa.flashsaleservice.payload;

import com.chuwa.flashsaleservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleOrderResponseEvent {
    private Status status;
    private Long orderId;
}

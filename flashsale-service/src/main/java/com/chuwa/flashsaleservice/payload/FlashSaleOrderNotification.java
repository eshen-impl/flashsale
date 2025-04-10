package com.chuwa.flashsaleservice.payload;

import com.chuwa.flashsaleservice.enums.FlashSaleOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FlashSaleOrderNotification {
    private FlashSaleOrderStatus flashSaleOrderStatus;
    private String message;
}

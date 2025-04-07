package com.chuwa.flashsaleservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FlashSaleOrderItem {
    private Long itemId;
    private String itemName;
    private int quantity;
    private BigDecimal unitPrice;
}

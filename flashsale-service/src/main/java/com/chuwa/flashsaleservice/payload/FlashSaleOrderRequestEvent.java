package com.chuwa.flashsaleservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FlashSaleOrderRequestEvent {

    private Long flashSaleId;
    private UUID userId;
    private BigDecimal totalAmount;
    private String items;
    private String currency;
}

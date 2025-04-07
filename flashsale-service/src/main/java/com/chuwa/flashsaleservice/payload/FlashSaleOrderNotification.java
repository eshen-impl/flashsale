package com.chuwa.flashsaleservice.payload;

import com.chuwa.flashsaleservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FlashSaleOrderNotification {
    private Status status;
    private String message;
}

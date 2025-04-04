package com.chuwa.orderservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDTO {

    private Long shippingAddressId;
    private Long billingAddressId;
    private Long paymentMethodId;
}

package com.chuwa.orderservice.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {


    private Long paymentMethodId;
    private PaymentType type;

    private String cardNumber;

    private String nameOnCard;

    private Date expirationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    enum PaymentType {
        CREDIT_CARD, DEBIT_CARD
    }
}

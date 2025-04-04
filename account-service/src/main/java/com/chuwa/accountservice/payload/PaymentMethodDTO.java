package com.chuwa.accountservice.payload;

import com.chuwa.accountservice.model.enumtype.PaymentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "Name on card is required")
    private String nameOnCard;

//    @NotBlank(message = "Expiration date is required")
//    @FutureOrPresent(message = "Expiration date cannot be in the past")
    private Date expirationDate;
}

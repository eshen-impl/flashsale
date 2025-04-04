package com.chuwa.orderservice.payload;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonTypeName("CartItem")
public class CartItem implements Serializable {
    private String itemId;
    private String itemName;
    private int quantity;
    private double unitPrice;

}


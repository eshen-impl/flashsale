package com.chuwa.itemservice.payload;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private String itemId;

    private String itemName;

    private String upc;

    private double unitPrice;

    private int stock;

    private LocalDate startDate;

    private int saleSession;
}

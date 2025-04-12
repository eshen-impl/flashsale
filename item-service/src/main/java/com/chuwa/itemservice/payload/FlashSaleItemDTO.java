package com.chuwa.itemservice.payload;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleItemDTO {
    private Long flashSaleId;
    private Long itemId;
    private String itemName;
    private String upc;
    private String imageUrlsJson;
    private String description;
    private String brand;
    private BigDecimal originalPrice;
    private BigDecimal flashPrice;
    private Integer stock;
    private LocalDate saleDate;
    private Integer saleStartTime;
    private Integer saleEndTime;
    private Integer purchaseLimit;

}

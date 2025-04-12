package com.chuwa.itemservice.entity;

import com.chuwa.itemservice.util.IdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "flash_sale_items")
public class FlashSaleItem {

    @Id
    @Column(updatable = false, nullable = false)
    private Long flashSaleId;

    @PrePersist
    public void generateId() {
        this.flashSaleId = IdGenerator.generateId();
    }

    @Column(nullable = false)
    private Long itemId; // still keep reference to the original item

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false, unique = true)
    private String upc;

    @Lob
    @Column(nullable = false)
    private String imageUrlsJson;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private BigDecimal originalPrice;

    @Column(nullable = false)
    private BigDecimal flashPrice;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private LocalDate saleDate;

    @Column(nullable = false)
    private Integer saleStartTime;  // Represents session time (e.g., 10 for 10:00)

    @Column(nullable = false)
    private Integer saleEndTime;

    @Column(nullable = false)
    private Integer purchaseLimit;

}


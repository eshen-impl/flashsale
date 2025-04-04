package com.chuwa.itemservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private String itemId;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false, unique = true)
    private String upc;

    @Column(nullable = false)
    private double unitPrice;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private int saleSession;  // Represents session time (e.g., 10 for 10:00)

}


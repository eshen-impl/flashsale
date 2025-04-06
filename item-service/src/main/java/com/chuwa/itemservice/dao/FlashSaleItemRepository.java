package com.chuwa.itemservice.dao;

import com.chuwa.itemservice.entity.FlashSaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {
//    List<FlashSaleItem> findItemsByItemIdIn(List<Long> itemId);

    List<FlashSaleItem> findBySaleDate(LocalDate saleDate);

}
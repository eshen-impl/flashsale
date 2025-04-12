package com.chuwa.itemservice.dao;

import com.chuwa.itemservice.entity.FlashSaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {
//    List<FlashSaleItem> findItemsByItemIdIn(List<Long> itemId);

    List<FlashSaleItem> findBySaleDate(LocalDate saleDate);

    @Modifying
    @Query("UPDATE FlashSaleItem f SET f.stock = f.stock - 1 WHERE f.flashSaleId = :flashSaleId AND f.stock > 0")
    int decrementStock(@Param("flashSaleId") Long flashSaleId);

    @Query("SELECT f.stock FROM FlashSaleItem f WHERE f.flashSaleId = :flashSaleId")
    Integer findStockByItemId(@Param("flashSaleId") Long flashSaleId);

    @Modifying
    @Query("UPDATE FlashSaleItem f SET f.stock = f.stock + 1 WHERE f.flashSaleId = :flashSaleId")
    int incrementStock(@Param("flashSaleId") Long flashSaleId);
}
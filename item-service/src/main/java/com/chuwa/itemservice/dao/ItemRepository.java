package com.chuwa.itemservice.dao;

import com.chuwa.itemservice.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface ItemRepository extends JpaRepository<Item, String> {
    List<Item> findItemsByItemIdIn(List<String> itemId);

    List<Item> findByStartDate(LocalDate startDate);

}
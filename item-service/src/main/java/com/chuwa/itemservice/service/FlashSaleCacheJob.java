package com.chuwa.itemservice.service;

import com.chuwa.itemservice.entity.Item;

import java.time.LocalDate;
import java.util.List;

public interface FlashSaleCacheJob {

    String scheduledDailyCache();
    void cacheFlashSaleListAndItems(LocalDate date, List<Item> items);


}

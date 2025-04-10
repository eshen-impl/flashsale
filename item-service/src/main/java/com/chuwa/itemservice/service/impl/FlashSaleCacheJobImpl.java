package com.chuwa.itemservice.service.impl;

import com.chuwa.itemservice.dao.FlashSaleItemRepository;
import com.chuwa.itemservice.entity.FlashSaleItem;
import com.chuwa.itemservice.service.FlashSaleCacheJob;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FlashSaleCacheJobImpl implements FlashSaleCacheJob {
    private final FlashSaleItemRepository flashSaleItemRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String FLASH_SALE_ITEM_INFO_KEY = "flashsale:item:info:";
    private static final String FLASH_SALE_ITEM_STOCK_KEY = "flashsale:item:stock:";
    private static final String FLASH_SALE_ITEM_ORDER_KEY = "flashsale:item:order:";

    public FlashSaleCacheJobImpl(FlashSaleItemRepository flashSaleItemRepository, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.flashSaleItemRepository = flashSaleItemRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


//    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight
    public String scheduledDailyCache() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        redisTemplate.delete(FLASH_SALE_ITEM_INFO_KEY + yesterday);
        redisTemplate.delete(FLASH_SALE_ITEM_STOCK_KEY + yesterday);
        redisTemplate.delete(FLASH_SALE_ITEM_ORDER_KEY + yesterday);
        List<FlashSaleItem> flashSaleItems = flashSaleItemRepository.findBySaleDate(today);
        cacheFlashSaleItemsAndStock(today, flashSaleItems);
        return "Completed daily job for flash sale items and stock cache.";
    }



    private void cacheFlashSaleItemsAndStock(LocalDate date, List<FlashSaleItem> flashSaleItems) {
        String redisInfoKey = FLASH_SALE_ITEM_INFO_KEY + date;
        String redisStockKey = FLASH_SALE_ITEM_STOCK_KEY + date;
        for (FlashSaleItem flashSaleItem : flashSaleItems) {
            try {
                String itemJson = objectMapper.writeValueAsString(flashSaleItem);
                redisTemplate.opsForHash().put(redisInfoKey, String.valueOf(flashSaleItem.getFlashSaleId()), itemJson);
                redisTemplate.opsForHash().put(redisStockKey, String.valueOf(flashSaleItem.getFlashSaleId()), String.valueOf(flashSaleItem.getStock()));
            } catch (Exception e) {
                log.warn("Failed to write flash sale item info and stock to Redis: " + e.getMessage());
            }
        }
        log.info("Cached " + flashSaleItems.size() + " flash sale items for " + date);
    }

}

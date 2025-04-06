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
    private static final String FLASH_SALE_CACHE_KEY = "flashsale:";

    public FlashSaleCacheJobImpl(FlashSaleItemRepository flashSaleItemRepository, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.flashSaleItemRepository = flashSaleItemRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


//    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight
    public String scheduledDailyCache() {
        LocalDate today = LocalDate.now();
        redisTemplate.delete(FLASH_SALE_CACHE_KEY + today.minusDays(1));
        List<FlashSaleItem> flashSaleItems = flashSaleItemRepository.findBySaleDate(today);
        cacheFlashSaleListAndItems(today, flashSaleItems);
        return "Completed daily job for flash sale flashSaleItems cache.";
    }



    public void cacheFlashSaleListAndItems(LocalDate date, List<FlashSaleItem> flashSaleItems) {
        String redisKey = FLASH_SALE_CACHE_KEY + date;
        for (FlashSaleItem flashSaleItem : flashSaleItems) {
            try {
                String itemJson = objectMapper.writeValueAsString(flashSaleItem);
                redisTemplate.opsForHash().put(redisKey, String.valueOf(flashSaleItem.getFlashSaleId()), itemJson);
            } catch (Exception e) {
                log.warn("Failed to write flash sale flashSaleItem to Redis: " + e.getMessage());
            }
        }
        log.info("Cached " + flashSaleItems.size() + " flash sale flashSaleItems for " + date);
    }

}

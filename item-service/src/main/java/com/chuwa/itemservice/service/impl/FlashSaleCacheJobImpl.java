package com.chuwa.itemservice.service.impl;

import com.chuwa.itemservice.dao.ItemRepository;
import com.chuwa.itemservice.entity.Item;
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
    private final ItemRepository itemRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String FLASH_SALE_CACHE_KEY = "flashsale:";

    public FlashSaleCacheJobImpl(ItemRepository itemRepository, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.itemRepository = itemRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


//    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight
    public String scheduledDailyCache() {
        LocalDate today = LocalDate.now();
        redisTemplate.delete(FLASH_SALE_CACHE_KEY + today.minusDays(1));
        List<Item> items = itemRepository.findByStartDate(today);
        cacheFlashSaleListAndItems(today, items);
        return "Completed daily job for flash sale items cache.";
    }



    public void cacheFlashSaleListAndItems(LocalDate date, List<Item> items) {
        String redisKey = FLASH_SALE_CACHE_KEY + date;
        for (Item item : items) {
            try {
                String itemJson = objectMapper.writeValueAsString(item);
                redisTemplate.opsForHash().put(redisKey, item.getItemId(), itemJson);
            } catch (Exception e) {
                log.warn("Failed to write flash sale item to Redis: " + e.getMessage());
            }
        }
        log.info("Cached " + items.size() + " flash sale items for " + date);
    }

}

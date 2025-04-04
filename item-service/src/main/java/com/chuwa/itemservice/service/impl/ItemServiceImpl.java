package com.chuwa.itemservice.service.impl;

import com.chuwa.itemservice.dao.ItemRepository;
import com.chuwa.itemservice.entity.Item;
import com.chuwa.itemservice.exception.ResourceNotFoundException;
import com.chuwa.itemservice.payload.ItemDTO;
import com.chuwa.itemservice.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String FLASH_SALE_CACHE_KEY = "flashsale:"; // Redis key prefix

    public ItemServiceImpl(ItemRepository itemRepository, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.itemRepository = itemRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

//    public ItemDTO createItem(ItemDTO itemDTO) {
//        Item item = new Item();
//        mapToItem(item, itemDTO);
//
//        Item savedItem = itemRepository.save(item);
//        return convertToDTO(savedItem);
//    }

    public ItemDTO getItemById(String itemId) {

        String cachedItem = redisTemplate.opsForValue().get(FLASH_SALE_CACHE_KEY + itemId);

        if (cachedItem != null) {
            try {
                return convertToDTO(objectMapper.readValue(cachedItem, Item.class));
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        return convertToDTO(item);
    }


//    public ItemDTO updateItem(String id, ItemDTO itemDTO) {
//        Item existingItem = itemRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
//
//        mapToItem(existingItem, itemDTO);
//        Item updatedItem = itemRepository.save(existingItem);
//
//        return convertToDTO(updatedItem);
//    }

//    public void deleteItem(String id) {
//        itemRepository.deleteById(id);
//    }

    public Page<ItemDTO> getAllItems(Pageable pageable) {
        Page<Item> items = itemRepository.findAll(pageable);
        return items.map(this::convertToDTO);

    }

    public List<ItemDTO> getTodayFlashSaleItems() {
        LocalDate today = LocalDate.now();

        List<ItemDTO> cachedItems = getItemsFromCache(today);

        if (!cachedItems.isEmpty()) {
            return cachedItems;
        }

        List<Item> items = itemRepository.findByStartDate(today);

        // Cache them for later use
        items.forEach(this::cacheItem);

        return items.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private List<ItemDTO> getItemsFromCache(LocalDate today) {
        return redisTemplate.keys(FLASH_SALE_CACHE_KEY + "*").stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, Item.class);
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                        return null;
                    }
                })
                .filter(item -> item != null && item.getStartDate().equals(today))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void cacheItem(Item item) {
        try {
            String itemJson = objectMapper.writeValueAsString(item);
            redisTemplate.opsForValue().set(FLASH_SALE_CACHE_KEY + item.getItemId(), itemJson);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

//    public Map<String, Integer> getAvailableUnits(List<String> itemIds) {
//        return itemRepository.findItemsByItemIdIn(itemIds).stream()
//                .collect(Collectors.toMap(Item::getItemId, Item::getAvailableUnits));
//    }

//    private void mapToItem(Item item, ItemDTO itemDTO) {
//        item.setItemName(itemDTO.getItemName());
//        item.setUpc(itemDTO.getUpc());
//        item.setUnitPrice(itemDTO.getUnitPrice());
//        item.setPictureUrls(itemDTO.getPictureUrls());
//        item.setAvailableUnits(itemDTO.getAvailableUnits());
//    }
//
    private ItemDTO convertToDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setItemId(item.getItemId());
        itemDTO.setItemName(item.getItemName());
        itemDTO.setUpc(item.getUpc());
        itemDTO.setUnitPrice(item.getUnitPrice());
        itemDTO.setStock(item.getStock());
        itemDTO.setStartDate(item.getStartDate());
        itemDTO.setSaleSession(item.getSaleSession());

        return itemDTO;
    }




}

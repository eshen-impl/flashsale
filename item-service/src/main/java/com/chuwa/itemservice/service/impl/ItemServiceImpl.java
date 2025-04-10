package com.chuwa.itemservice.service.impl;

import com.chuwa.itemservice.dao.FlashSaleItemRepository;
import com.chuwa.itemservice.entity.FlashSaleItem;
import com.chuwa.itemservice.exception.ResourceNotFoundException;
import com.chuwa.itemservice.payload.FlashSaleItemDTO;
import com.chuwa.itemservice.service.FlashSaleCacheJob;
import com.chuwa.itemservice.service.ItemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final FlashSaleItemRepository flashSaleItemRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String FLASH_SALE_ITEM_INFO_KEY = "flashsale:item:info:";

    public ItemServiceImpl(FlashSaleItemRepository flashSaleItemRepository, StringRedisTemplate redisTemplate, ObjectMapper objectMapper, FlashSaleCacheJob flashSaleCacheJob) {
        this.flashSaleItemRepository = flashSaleItemRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public FlashSaleItemDTO createItem(FlashSaleItemDTO flashSaleItemDTO) {
        FlashSaleItem flashSaleItem = objectMapper.convertValue(flashSaleItemDTO, FlashSaleItem.class);
        flashSaleItem.setFlashSaleId(null);

        FlashSaleItem savedFlashSaleItem = flashSaleItemRepository.save(flashSaleItem);
        return convertToDTO(savedFlashSaleItem);
    }

    public FlashSaleItemDTO getItemById(Long itemId) {
        //TODO: split normal item and flash sale item entity/repository
        FlashSaleItem flashSaleItem = flashSaleItemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        return convertToDTO(flashSaleItem);
    }

    public FlashSaleItemDTO getFlashSaleItemById(Long flashSaleId) {
        String cachedItem = (String) redisTemplate.opsForHash().get(FLASH_SALE_ITEM_INFO_KEY + LocalDate.now(), String.valueOf(flashSaleId));

        if (cachedItem != null) {
            try {
                return objectMapper.readValue(cachedItem, FlashSaleItemDTO.class);
            } catch (Exception e) {
                log.warn("Failed to parse flash sale flashSaleItem JSON: " + e.getMessage());
            }
        }

        throw new ResourceNotFoundException("FlashSaleItem not found");
    }


//    public FlashSaleItemDTO updateItem(String id, FlashSaleItemDTO itemDTO) {
//        FlashSaleItem existingItem = flashSaleItemRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("FlashSaleItem not found"));
//
//        mapToItem(existingItem, itemDTO);
//        FlashSaleItem updatedItem = flashSaleItemRepository.save(existingItem);
//
//        return convertToDTO(updatedItem);
//    }

//    public void deleteItem(String id) {
//        flashSaleItemRepository.deleteById(id);
//    }

    public Page<FlashSaleItemDTO> getAllItems(Pageable pageable) {
        Page<FlashSaleItem> items = flashSaleItemRepository.findAll(pageable);
        return items.map(this::convertToDTO);

    }

    public List<FlashSaleItemDTO> getTodayFlashSaleItems() {
        LocalDate today = LocalDate.now();

        List<FlashSaleItemDTO> cachedItems = getItemsFromCache(today);

        if (!cachedItems.isEmpty()) {
            return cachedItems;
        }

        throw new ResourceNotFoundException("No flash sale items found for today...");
    }

    @Override
    @Transactional
    public Boolean tryDecrementStock(Long flashSaleId) {
        return flashSaleItemRepository.decrementStock(flashSaleId) > 0;
    }

    private List<FlashSaleItemDTO> getItemsFromCache(LocalDate today) {
         return redisTemplate.opsForHash().values(FLASH_SALE_ITEM_INFO_KEY + today)
                 .stream()
                 .map(val -> {
                     try {
                         return objectMapper.readValue(val.toString(), FlashSaleItem.class);
                     } catch (JsonProcessingException e) {
                         log.warn("Failed to parse flash sale item JSON: " + e.getMessage());
                         return null;
                     }
                 })
                 .filter(Objects::nonNull)
                 .map(this::convertToDTO)
                 .collect(Collectors.toList());
    }



//    public Map<String, Integer> getAvailableUnits(List<String> itemIds) {
//        return flashSaleItemRepository.findItemsByItemIdIn(itemIds).stream()
//                .collect(Collectors.toMap(FlashSaleItem::getItemId, FlashSaleItem::getAvailableUnits));
//    }


    private FlashSaleItemDTO convertToDTO(FlashSaleItem flashSaleItem) {
        return objectMapper.convertValue(flashSaleItem, FlashSaleItemDTO.class);
    }


}

package com.chuwa.itemservice.service;

import com.chuwa.itemservice.payload.FlashSaleItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    FlashSaleItemDTO createItem(FlashSaleItemDTO flashSaleItemDTO);

    FlashSaleItemDTO getItemById(Long itemId);
    FlashSaleItemDTO getFlashSaleItemById(Long flashSaleId);
//    FlashSaleItemDTO updateItem(String id, FlashSaleItemDTO itemDTO);
//    void deleteItem(String id);
    Page<FlashSaleItemDTO> getAllItems(Pageable pageable);
    List<FlashSaleItemDTO> getTodayFlashSaleItems();

    Boolean tryDecrementStock(Long flashSaleId);

//    Map<String, Integer> getAvailableUnits(List<String> itemIds);
}

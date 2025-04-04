package com.chuwa.itemservice.controller;

import com.chuwa.itemservice.payload.ItemDTO;
import com.chuwa.itemservice.service.FlashSaleCacheJob;
import com.chuwa.itemservice.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {


    private final ItemService itemService;
    private final FlashSaleCacheJob flashSaleCacheJob;

    public ItemController(ItemService itemService, FlashSaleCacheJob flashSaleCacheJob) {
        this.itemService = itemService;
        this.flashSaleCacheJob = flashSaleCacheJob;
    }


//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @PostMapping
//    @Operation(summary = "Post new product and its details",
//            description = "Required to have role: 'ROLE_AMIN'")
//    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) {
//        return ResponseEntity.ok(itemService.createItem(itemDTO));
//    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific product's details",
            description = "Accessible to all guests and authenticated users")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable("id") String id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @PutMapping("/{id}")
//    @Operation(summary = "Update an existing product and its details",
//            description = "Required to have role: 'ROLE_AMIN'")
//    public ResponseEntity<ItemDTO> updateItem(@PathVariable("id") String id,  @RequestBody ItemDTO itemDTO) {
//        return ResponseEntity.ok(itemService.updateItem(id, itemDTO));
//
//    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete an existing product.",
//            description = "Required to have role: 'ROLE_AMIN'")
//    public ResponseEntity<Void> deleteItem(@PathVariable("id") String id) {
//        itemService.deleteItem(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/mgmt/inventory")
    @Operation(summary = "Get an inventory list of all available products",
            description = "Required to have role: 'ROLE_AMIN'")
    public ResponseEntity<Page<ItemDTO>> getAllItems(@RequestParam(defaultValue = "0", name = "page") int page,
                                                     @RequestParam(defaultValue = "10", name = "size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemDTO> itemDTOs = itemService.getAllItems(pageable);
        return new ResponseEntity<>(itemDTOs, HttpStatus.OK);

    }

//    @PostMapping("/availability")
//    @Operation(summary = "Get available units for a list of item IDs",
//            description = "Returns a map where the key is the item ID and the value is the available stock for that item.")
//    public ResponseEntity<Map<String, Integer>> getAvailableUnits(@RequestBody List<String> itemIds) {
//        Map<String, Integer> availability = itemService.getAvailableUnits(itemIds);
//        return ResponseEntity.ok(availability);
//    }


    @GetMapping("/flashsale")
    public ResponseEntity<List<ItemDTO>> getTodayFlashSale() {

        return ResponseEntity.ok(itemService.getTodayFlashSaleItems());
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/mgmt/flashsale")
    public ResponseEntity<String> cacheFlashSale() {

        return ResponseEntity.ok(flashSaleCacheJob.cacheFlashSaleItems());
    }
}

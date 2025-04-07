package com.chuwa.flashsaleservice.controller;


import com.chuwa.flashsaleservice.payload.FlashSaleOrderNotification;
import com.chuwa.flashsaleservice.service.FlashSaleOrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/flashsale")
public class FlashSaleOrderController {

    private final FlashSaleOrderService flashSaleOrderService;

    public FlashSaleOrderController(FlashSaleOrderService flashSaleOrderService) {
        this.flashSaleOrderService = flashSaleOrderService;
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit an order for a flash sale item. ",
            description = "If success, return details of this order. " + "Required to be authenticated (have signed in) ")
    public ResponseEntity<FlashSaleOrderNotification> submitOrder(@RequestParam("flashSaleId") Long flashSaleId, @RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        return ResponseEntity.ok(flashSaleOrderService.submitOrder(flashSaleId, userId));
    }




}


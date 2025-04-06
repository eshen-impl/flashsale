package com.chuwa.orderservice.controller;


import com.chuwa.orderservice.payload.OrderDTO;
import com.chuwa.orderservice.service.FlashSaleOrderService;
import com.chuwa.orderservice.service.OrderService;
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
            description = "Return details of this order. " + "Required to be authenticated (have signed in) ")
    public ResponseEntity<OrderDTO> submitOrder(@RequestParam("flashSaleId") Long flashSaleId, @RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        return ResponseEntity.ok(flashSaleOrderService.submitOrder(flashSaleId, userId));
    }




}


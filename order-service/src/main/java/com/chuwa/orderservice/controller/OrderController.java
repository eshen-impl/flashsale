package com.chuwa.orderservice.controller;

import com.chuwa.orderservice.payload.CreateOrderRequestDTO;
import com.chuwa.orderservice.payload.OrderDTO;
import com.chuwa.orderservice.payload.RefundRequestDTO;
import com.chuwa.orderservice.payload.UpdateOrderRequestDTO;
import com.chuwa.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    @Operation(summary = "Submit a new order for all the added items in cart. ",
            description = "Return details of this order. " + "Required to be authenticated (have signed in) ")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequestDTO createOrderRequestDTO, @RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        return ResponseEntity.ok(orderService.createOrder(userId, createOrderRequestDTO));
    }

    @PutMapping("/cancel")
    @Operation(summary = "Cancel an existing order.",
            description = "Order status can changed to be cancelled before shipping. "
                    + "Required to be authenticated (have signed in)")
    public ResponseEntity<OrderDTO> cancelOrder(@RequestParam("orderId") UUID orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @PutMapping("/update")
    @Operation(summary = "Update an existing order (total amount, items, shipping/billing address and payment method). ",
            description = "Currently items need to be in JSON string format. "
                    + "Required to be authenticated (have signed in)")
    public ResponseEntity<OrderDTO> updateOrder(@RequestBody UpdateOrderRequestDTO updateOrderRequestDTO) {
        return ResponseEntity.ok(orderService.updateOrder(updateOrderRequestDTO));
    }

    @PutMapping("/refund")
    @Operation(summary = "Request refund for an existing order. ",
            description = "Required to be authenticated (have signed in)")
    public ResponseEntity<OrderDTO> refundOrder(@RequestBody RefundRequestDTO refundRequestDTO) {
        return ResponseEntity.ok(orderService.refundOrder(refundRequestDTO));
    }

    @GetMapping("/history")
    @Operation(summary = "Get all history orders and their details for the current user. ",
            description =  "Required to be authenticated (have signed in) ")
    public ResponseEntity<Page<OrderDTO>> getUserOrders(@RequestParam(defaultValue = "0", name = "page") int page,
                                                        @RequestParam(defaultValue = "5", name = "size") int size,
                                                        @RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        return ResponseEntity.ok(orderService.getUserOrders(page, size, userId));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get a specific order and its details for the current user.",
            description =  "Required to be authenticated (have signed in) ")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }
}


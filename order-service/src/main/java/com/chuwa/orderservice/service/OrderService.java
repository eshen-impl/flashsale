package com.chuwa.orderservice.service;

import com.chuwa.orderservice.payload.PaymentEvent;
import com.chuwa.orderservice.payload.ShippingEvent;
import com.chuwa.orderservice.payload.CreateOrderRequestDTO;
import com.chuwa.orderservice.payload.OrderDTO;
import com.chuwa.orderservice.payload.RefundRequestDTO;
import com.chuwa.orderservice.payload.UpdateOrderRequestDTO;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDTO createOrder(UUID userId, CreateOrderRequestDTO createOrderRequestDTO);
    OrderDTO updateOrder(UpdateOrderRequestDTO updateRequest);
    OrderDTO cancelOrder(UUID orderId);
    OrderDTO refundOrder(RefundRequestDTO refundRequestDTO);
    Page<OrderDTO> getUserOrders(int page, int size, UUID userId);
    OrderDTO getOrderById(UUID orderId);
    void processPaymentResponse(PaymentEvent response);
    void processShippingResponse(ShippingEvent event);

}

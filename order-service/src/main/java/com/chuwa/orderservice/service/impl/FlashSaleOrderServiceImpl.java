package com.chuwa.orderservice.service.impl;

import com.chuwa.orderservice.client.ItemClient;
import com.chuwa.orderservice.dao.OrderRepository;
import com.chuwa.orderservice.entity.Order;
import com.chuwa.orderservice.enums.FlashSaleOrderStatus;
import com.chuwa.orderservice.enums.OrderStatus;
import com.chuwa.orderservice.enums.PaymentStatus;
import com.chuwa.orderservice.exception.InsufficientStockException;
import com.chuwa.orderservice.payload.FlashSaleOrderRequestEvent;
import com.chuwa.orderservice.payload.FlashSaleOrderResponseEvent;
import com.chuwa.orderservice.producer.FlashSaleOrderEventProducer;
import com.chuwa.orderservice.service.FlashSaleOrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Slf4j
@Service
public class FlashSaleOrderServiceImpl implements FlashSaleOrderService {
    private final ItemClient itemClient;
    private final OrderRepository orderRepository;
    private final FlashSaleOrderEventProducer flashSaleOrderEventProducer;
    private final StringRedisTemplate redisTemplate;
    private static final String FLASH_SALE_ITEM_STOCK_KEY = "flashsale:item:stock:";
    private static final String FLASH_SALE_ITEM_ORDER_KEY = "flashsale:item:order:";

    public FlashSaleOrderServiceImpl(ItemClient itemClient, OrderRepository orderRepository, FlashSaleOrderEventProducer flashSaleOrderEventProducer, StringRedisTemplate redisTemplate) {
        this.itemClient = itemClient;
        this.orderRepository = orderRepository;
        this.flashSaleOrderEventProducer = flashSaleOrderEventProducer;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
//    @CircuitBreaker(name = "createFlashSaleOrder", fallbackMethod = "fallbackForCreateFlashSaleOrder")
    public void createOrder(FlashSaleOrderRequestEvent flashSaleOrderRequestEvent) {
        if (!itemClient.decrementStock(flashSaleOrderRequestEvent.getFlashSaleId())) {

        //rewind order placement count
        redisTemplate.opsForHash().increment(
                FLASH_SALE_ITEM_ORDER_KEY + LocalDate.now(),
                flashSaleOrderRequestEvent.getUserId() + ":" + flashSaleOrderRequestEvent.getFlashSaleId(),
                -1);

        flashSaleOrderEventProducer.sendToFlashSale(
                new FlashSaleOrderResponseEvent(FlashSaleOrderStatus.INSUFFICIENT_STOCK, null, "out of stock"));

        } else {
            Order order = new Order();
            order.setUserId(flashSaleOrderRequestEvent.getUserId());
            order.setOrderStatus(OrderStatus.CREATED);
            order.setTotalAmount(flashSaleOrderRequestEvent.getTotalAmount());
            order.setFlashSaleId(flashSaleOrderRequestEvent.getFlashSaleId());
            LocalDateTime now = LocalDateTime.now();
            order.setCreatedAt(now);
            order.setUpdatedAt(now);
            order.setItems(flashSaleOrderRequestEvent.getItems());
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setTransactionKey(UUID.randomUUID());
            order.setCurrency(Currency.getInstance(flashSaleOrderRequestEvent.getCurrency()));

            Order savedOrder = orderRepository.save(order);

            flashSaleOrderEventProducer.sendToFlashSale(
                    new FlashSaleOrderResponseEvent(FlashSaleOrderStatus.CONFIRMED, savedOrder.getOrderId(), ""));
        }

    }

    public void fallbackForCreateFlashSaleOrder(FlashSaleOrderRequestEvent flashSaleOrderRequestEvent, Throwable throwable) {
        //rewind stock and order placement count in redis for other exceptions
        redisTemplate.opsForHash().increment(
                FLASH_SALE_ITEM_STOCK_KEY + LocalDate.now(),
                flashSaleOrderRequestEvent.getFlashSaleId() + "",
                1);
        redisTemplate.opsForHash().increment(
                FLASH_SALE_ITEM_ORDER_KEY + LocalDate.now(),
                flashSaleOrderRequestEvent.getUserId() + ":" + flashSaleOrderRequestEvent.getFlashSaleId(),
                -1);

        flashSaleOrderEventProducer.sendToFlashSale(
                new FlashSaleOrderResponseEvent(FlashSaleOrderStatus.FAILED, null, throwable.getMessage()));

    }
}

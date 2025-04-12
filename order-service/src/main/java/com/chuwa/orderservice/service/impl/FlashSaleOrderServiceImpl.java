package com.chuwa.orderservice.service.impl;

import com.chuwa.orderservice.client.ItemClient;
import com.chuwa.orderservice.dao.OrderRepository;
import com.chuwa.orderservice.entity.Order;
import com.chuwa.orderservice.enums.FlashSaleOrderStatus;
import com.chuwa.orderservice.enums.OrderStatus;
import com.chuwa.orderservice.enums.PaymentStatus;
import com.chuwa.orderservice.payload.DelayedOrderEntry;
import com.chuwa.orderservice.payload.FlashSaleOrderRequestEvent;
import com.chuwa.orderservice.payload.FlashSaleOrderResponseEvent;
import com.chuwa.orderservice.producer.FlashSaleOrderEventProducer;
import com.chuwa.orderservice.service.FlashSaleOrderService;
import com.chuwa.orderservice.util.JsonUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FlashSaleOrderServiceImpl implements FlashSaleOrderService {
    private final ItemClient itemClient;
    private final OrderRepository orderRepository;
    private final FlashSaleOrderEventProducer flashSaleOrderEventProducer;
    private final StringRedisTemplate redisTemplate;
    private static final String FLASH_SALE_ITEM_STOCK_KEY = "flashsale:item:stock:";
    private static final String FLASH_SALE_ITEM_ORDER_KEY = "flashsale:item:order:";
    private static final String ORDER_DELAY_KEY = "flashsale:order:pending";

    public FlashSaleOrderServiceImpl(ItemClient itemClient, OrderRepository orderRepository, FlashSaleOrderEventProducer flashSaleOrderEventProducer, StringRedisTemplate redisTemplate) {
        this.itemClient = itemClient;
        this.orderRepository = orderRepository;
        this.flashSaleOrderEventProducer = flashSaleOrderEventProducer;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CircuitBreaker(name = "createFlashSaleOrder", fallbackMethod = "fallbackForCreateFlashSaleOrder")
    public void createOrder(FlashSaleOrderRequestEvent flashSaleOrderRequestEvent) {
        Long flashSaleId = flashSaleOrderRequestEvent.getFlashSaleId();
        UUID userId = flashSaleOrderRequestEvent.getUserId();

        if (!itemClient.decrementStock(flashSaleId)) {
            rollbackRedisPurchaseHistory(flashSaleId, userId);

            flashSaleOrderEventProducer.sendToFlashSale(
                    new FlashSaleOrderResponseEvent(FlashSaleOrderStatus.INSUFFICIENT_STOCK,
                            null, "out of stock", userId));

        } else {
            Order order = new Order();
            order.setUserId(userId);
            order.setOrderStatus(OrderStatus.CREATED);
            order.setTotalAmount(flashSaleOrderRequestEvent.getTotalAmount());
            order.setFlashSaleId(flashSaleId);

            order.setItems(flashSaleOrderRequestEvent.getItems());
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setTransactionKey(UUID.randomUUID());
            order.setCurrency(Currency.getInstance(flashSaleOrderRequestEvent.getCurrency()));

            Order savedOrder = orderRepository.save(order);
            Long orderId = savedOrder.getOrderId();

            //cancel unpaid orders after 15 minutes
            long delayMillis = TimeUnit.MINUTES.toMillis(1);
            long scheduledTime = System.currentTimeMillis() + delayMillis;
            String entryJson = JsonUtil.toJson(new DelayedOrderEntry(orderId, flashSaleId, userId));
            redisTemplate.opsForZSet().add(ORDER_DELAY_KEY, entryJson, scheduledTime);

            flashSaleOrderEventProducer.sendToFlashSale(
                    new FlashSaleOrderResponseEvent(FlashSaleOrderStatus.CONFIRMED,
                            orderId, "", userId));
        }

    }

    public void fallbackForCreateFlashSaleOrder(FlashSaleOrderRequestEvent flashSaleOrderRequestEvent, Throwable throwable) {
        try {
            //rewind stock and order placement count in redis for other exceptions
            Long flashSaleId = flashSaleOrderRequestEvent.getFlashSaleId();
            UUID userId = flashSaleOrderRequestEvent.getUserId();

            rollbackRedisStock(flashSaleId);
            rollbackRedisPurchaseHistory(flashSaleId, userId);

            flashSaleOrderEventProducer.sendToFlashSale(
                    new FlashSaleOrderResponseEvent(FlashSaleOrderStatus.FAILED,
                            null, throwable.getMessage(), userId));
        } catch (Exception e) {
            log.warn("fallbackForCreateFlashSaleOrder exception: {}", e.getMessage());
        }

    }

    public void rollbackRedisStock(Long flashSaleId) {
        Integer stock = itemClient.getStock(flashSaleId);
        redisTemplate.opsForHash().put(
                FLASH_SALE_ITEM_STOCK_KEY + LocalDate.now(),
                flashSaleId + "",
                stock + "");
    }

    public void rollbackRedisPurchaseHistory(Long flashSaleId, UUID userId){
        redisTemplate.opsForHash().increment(
                FLASH_SALE_ITEM_ORDER_KEY + LocalDate.now(),
                userId + ":" + flashSaleId,
                -1);
    }
}

package com.chuwa.orderservice.scheduler;


import com.chuwa.orderservice.client.ItemClient;
import com.chuwa.orderservice.dao.OrderRepository;
import com.chuwa.orderservice.enums.OrderStatus;
import com.chuwa.orderservice.enums.PaymentStatus;
import com.chuwa.orderservice.payload.DelayedOrderEntry;
import com.chuwa.orderservice.service.FlashSaleOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.chuwa.orderservice.util.JsonUtil;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class OrderPaymentTimeoutProcessor {

    private final StringRedisTemplate redisTemplate;
    private final OrderRepository orderRepository;
    private final ItemClient itemClient;
    private final FlashSaleOrderService flashSaleOrderService;
    private static final String ORDER_DELAY_KEY = "flashsale:order:pending";

    public OrderPaymentTimeoutProcessor(StringRedisTemplate redisTemplate, OrderRepository orderRepository, ItemClient itemClient, FlashSaleOrderService flashSaleOrderService) {
        this.redisTemplate = redisTemplate;
        this.orderRepository = orderRepository;
        this.itemClient = itemClient;
        this.flashSaleOrderService = flashSaleOrderService;
    }

    @Scheduled(initialDelay = 10000, fixedRate = 30000) // runs every 30 seconds
    @Transactional(rollbackFor = Exception.class)
    public void checkUnpaidOrders() {
        long now = System.currentTimeMillis();

        Set<String> expiredOrderJsons = redisTemplate.opsForZSet()
                .rangeByScore(ORDER_DELAY_KEY, 0, now);

        if (expiredOrderJsons == null || expiredOrderJsons.isEmpty()) {
            return;
        }

        expiredOrderJsons.stream()
                .map(JsonUtil::fromJsonToDelayedOrderEntry)
                .filter(Objects::nonNull)
                .forEach(entry -> {
                    try {
                        processExpiredOrderEntry(entry);
                    } catch (Exception e) {
                        log.error("Failed to process order entry: {}, Error: {}", entry, e.getMessage());
                    }
                });

    }

    private void processExpiredOrderEntry(DelayedOrderEntry entry) {
        Long flashSaleId = entry.getFlashSaleId();
        UUID userId = entry.getUserId();
        Long orderId = entry.getOrderId();

        int row = orderRepository.cancelUnpaidOrder(
                orderId,
                OrderStatus.CANCELED,
                PaymentStatus.TIME_OUT,
                PaymentStatus.PAID
        );

        if (row > 0) {
            itemClient.incrementStock(flashSaleId);
            flashSaleOrderService.rollbackRedisStock(flashSaleId);
            flashSaleOrderService.rollbackRedisPurchaseHistory(flashSaleId, userId);

            log.info("Order {} cancelled due to timeout", orderId);
            // TODO: publish event to Kafka → WebSocket notify
        }

        redisTemplate.opsForZSet().remove(ORDER_DELAY_KEY, JsonUtil.toJson(entry));

    }
}

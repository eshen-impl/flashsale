package com.chuwa.orderservice.service.impl;

import com.chuwa.orderservice.client.AccountClient;
import com.chuwa.orderservice.client.ItemClient;
import com.chuwa.orderservice.client.PaymentClient;
import com.chuwa.orderservice.dao.OrderByUserRepository;
import com.chuwa.orderservice.dao.OrderRepository;
import com.chuwa.orderservice.entity.*;
import com.chuwa.orderservice.enums.*;
import com.chuwa.orderservice.exception.EmptyCartException;
import com.chuwa.orderservice.exception.InsufficientStockException;
import com.chuwa.orderservice.exception.ResourceNotFoundException;
import com.chuwa.orderservice.payload.*;
//import com.chuwa.orderservice.publisher.OrderEventPublisher;
import com.chuwa.orderservice.producer.OrderEventProducer;
import com.chuwa.orderservice.service.OrderService;
import com.chuwa.orderservice.util.CartRedisUtil;
import com.chuwa.orderservice.util.JsonUtil;
import com.chuwa.orderservice.util.UUIDUtil;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderByUserRepository orderByUserRepository;
    private final CartRedisUtil cartRedisUtil;
    private final ItemClient itemClient;
    private final AccountClient accountClient;
    private final PaymentClient paymentClient;
    private final OrderEventProducer orderEventProducer;

    public OrderServiceImpl(OrderRepository orderRepository, OrderByUserRepository orderByUserRepository, CartRedisUtil cartRedisUtil, ItemClient itemClient, AccountClient accountClient, PaymentClient paymentClient, OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.orderByUserRepository = orderByUserRepository;
        this.cartRedisUtil = cartRedisUtil;
        this.itemClient = itemClient;
        this.accountClient = accountClient;
        this.paymentClient = paymentClient;
        this.orderEventProducer = orderEventProducer;
    }

    @CircuitBreaker(name = "createOrder", fallbackMethod = "fallbackForCreateOrder")
    public OrderDTO createOrder(UUID userId, CreateOrderRequestDTO createOrderRequestDTO) {

        Order order = new Order();

        //retrieve cart items from redis
        String cartKey = "cart:"+ UUIDUtil.encodeUUID(userId);
        List<CartItem> items = cartRedisUtil.getCartItems(cartKey);
        if (items.isEmpty()) throw new EmptyCartException("Nothing in your cart yet. Please add something first!");

        //sync call item service to check requested units of each item in cart not exceeding available units
        validateOrderItems(items);

        //sync call account service to retrieve and set address and payment method snapshot on order
        setAddressAndPaymentMethod(order, createOrderRequestDTO);

        order.setOrderId(UUID.randomUUID());
        order.setUserId(userId);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setTotalAmount(BigDecimal.valueOf(items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum()));

        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        order.setItems(JsonUtil.toJson(items));
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setTransactionKey(UUID.randomUUID());
        order.setCurrency(Currency.getInstance("USD"));

        //sync call payment service to validate payment method
        try {
            if (order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
                Map<String, String> res = paymentClient.initiatePayment(
                        new ValidatePaymentRequestDTO(order.getOrderId(), order.getTransactionKey(),
                                                        createOrderRequestDTO.getPaymentMethodId(),
                                                        order.getTotalAmount(), order.getCurrency().getCurrencyCode()));
                if (Boolean.parseBoolean(res.get("isValid"))) {
                    order.setPaymentStatus(PaymentStatus.VALIDATED);
                    orderEventProducer.sendToShipping(convertToOrderEvent(order, OrderEventType.NEW_ORDER));
                } else {
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    order.setOrderStatus(OrderStatus.CANCELED);
                }
            }
        } catch (FeignException e) {
           //do nothing just proceed to save order with payment status as pending;
            // plan to do a scheduled check through all pending orders and call payment service later
        }

        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));
        cartRedisUtil.clearCart(cartKey);

        return convertToDTO(order);
    }

    public OrderDTO fallbackForCreateOrder(UUID userId, CreateOrderRequestDTO createOrderRequestDTO, Throwable throwable) {
        throw new RuntimeException("Service is unavailable, please try again later.\n" + throwable.getMessage());
    }

    @CircuitBreaker(name = "updateOrder", fallbackMethod = "fallbackForUpdateOrder")
    public OrderDTO updateOrder(UpdateOrderRequestDTO updateRequest) {
        Order order = orderRepository.findByOrderId(updateRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        BigDecimal originalTotal = order.getTotalAmount();

        List<CartItem> items = updateRequest.getItems();
        if (items != null) {
            validateOrderItems(items);
            order.setItems(JsonUtil.toJson(items));
            order.setTotalAmount(BigDecimal.valueOf(items.stream()
                    .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                    .sum()));
        }

        setAddressAndPaymentMethod(order, updateRequest);

        order.setOrderStatus(OrderStatus.UPDATED);
        order.setUpdatedAt(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.ADJUSTMENT_PENDING);
        order.setTransactionKey(UUID.randomUUID());
        order.setCurrency(Currency.getInstance("USD"));

        try {
            if (order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0
                && (!Objects.equals(originalTotal, order.getTotalAmount()) || updateRequest.getPaymentMethodId() != null)) {
                Map<String, String> res = paymentClient.initiatePayment(
                        new ValidatePaymentRequestDTO(order.getOrderId(), order.getTransactionKey(),
                                updateRequest.getPaymentMethodId(),
                                order.getTotalAmount(), order.getCurrency().getCurrencyCode()));
                if (Boolean.parseBoolean(res.get("isValid"))) {
                    order.setPaymentStatus(PaymentStatus.VALIDATED);
                    orderEventProducer.sendToShipping(convertToOrderEvent(order, OrderEventType.UPDATE_ORDER));
                } else {
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    order.setOrderStatus(OrderStatus.SUSPENDED);
                    orderEventProducer.sendToShipping(convertToOrderEvent(order, OrderEventType.SUSPEND_ORDER));
                }
            }
        } catch (FeignException e) {
            //do nothing just proceed to save order with payment status as pending;
            // plan to do a scheduled check through all adjustment pending orders and call payment service later
        }

        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));

        return convertToDTO(order);
    }

    public OrderDTO fallbackForUpdateOrder(UpdateOrderRequestDTO updateRequest, Throwable throwable) {
        throw new RuntimeException("Service is unavailable, please try again later.\n" + throwable.getMessage());
    }

    public OrderDTO cancelOrder(UUID orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Set<OrderStatus> nonCancelableStatuses = Set.of(OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.CANCELED);
        if (nonCancelableStatuses.contains(order.getOrderStatus())) {
            throw new IllegalStateException("Order cannot be canceled in status: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        order.setPaymentStatus(PaymentStatus.CANCELED);
        order.setUpdatedAt(LocalDateTime.now());
        try {
            paymentClient.cancelAuthorization(order.getTransactionKey());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (FeignException.Conflict e) {
            throw new IllegalStateException(e.getMessage());
        } catch (FeignException e) {
            throw new RuntimeException("Error calling cancel authorization from payment service." + e.getMessage());
        }
        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));
        orderEventProducer.sendToShipping(convertToOrderEvent(order, OrderEventType.CANCEL_ORDER));
        return convertToDTO(order);
    }

    @Override
    public OrderDTO refundOrder(RefundRequestDTO refundRequest) {
        Order order = orderRepository.findByOrderId(refundRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot request refund in order status: " + order.getOrderStatus());
        }

        order.setRefundStatus(PaymentRefundStatus.REFUND_REQUESTED);
        order.setUpdatedAt(LocalDateTime.now());
        try {
            paymentClient.initiateRefund(refundRequest);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (FeignException.Conflict e) {
            throw new IllegalStateException(e.getMessage());
        } catch (FeignException e) {
            throw new RuntimeException("Error calling initiate refund from payment service. " + e.getMessage());
        }
        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));

        return convertToDTO(order);
    }

    public List<OrderDTO> getUserOrders(UUID userId) {
        List<OrderByUser> orders = orderByUserRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public OrderDTO getOrderById(UUID orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }


    @Override
    public void processPaymentResponse(PaymentEvent event) {
        UUID orderId = event.getOrderId();
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (orderOpt.isEmpty()) return;

        Order order = orderOpt.get();
        if (event.getEventType() == PaymentEventType.PAYMENT_CHARGE_SUCCEED) {
            order.setOrderStatus(OrderStatus.SHIPPED);
            order.setPaymentStatus(PaymentStatus.PAID);
        } else if (event.getEventType() == PaymentEventType.PAYMENT_CHARGE_FAILED) {
            order.setOrderStatus(OrderStatus.SUSPENDED);
            order.setPaymentStatus(PaymentStatus.FAILED);
        } else if (event.getEventType() == PaymentEventType.PAYMENT_REFUNDED) {
            order.setRefundStatus(PaymentRefundStatus.REFUNDED);
            order.setRefundedAmount(event.getRefundedAmount());
        } else if (event.getEventType() == PaymentEventType.PAYMENT_REFUND_DENIED) {
            order.setRefundStatus(PaymentRefundStatus.NOT_REFUNDED);
        }
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));
    }

    public void processShippingResponse(ShippingEvent event) {
        UUID orderId = event.getOrderId();
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (orderOpt.isEmpty()) return;

        Order order = orderOpt.get();
        if (event.getEventType() == ShippingEventType.DELIVERED) {
            order.setOrderStatus(OrderStatus.DELIVERED);
        }
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));

    }

    private void validateOrderItems(List<CartItem> orderItems) {
        List<String> itemIds = orderItems.stream().map(CartItem::getItemId).collect(Collectors.toList());

        Map<String, Integer> availableUnits = itemClient.getAvailableUnits(itemIds);

        List<String> insufficientItems = new ArrayList<>();

        for (CartItem item : orderItems) {
            int requestedQty = item.getQuantity();
            int availableQty = availableUnits.getOrDefault(item.getItemId(), 0);

            if (requestedQty > availableQty) {
                insufficientItems.add("Item: " + item.getItemId() + " (Requested: "
                        + requestedQty + ", Available: " + availableQty + ")");
            }
        }

        if (!insufficientItems.isEmpty()) {
            throw new InsufficientStockException("Insufficient stock for the following items: " + String.join("; ", insufficientItems));
        }
    }

    private void setAddressAndPaymentMethod(Order order, CreateOrderRequestDTO orderRequest){

        try {
            Long billingAddressId = orderRequest.getBillingAddressId();
            Long shippingAddressId = orderRequest.getShippingAddressId();
            Long paymentMethodId = orderRequest.getPaymentMethodId();

            if (billingAddressId != null) {
                order.setBillingAddress(JsonUtil.toJson(accountClient.getAddress(billingAddressId)));
            }
            if (shippingAddressId != null) {
                order.setShippingAddress(JsonUtil.toJson(accountClient.getAddress(shippingAddressId)));
            }
            if (paymentMethodId != null) {
                order.setPaymentMethod(JsonUtil.toJson(hideSensitivePaymentMethodInfo(accountClient.getPaymentMethod(paymentMethodId))));
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Address or Payment Method not found in Account Service");
        } catch (FeignException e) {
            throw new RuntimeException("Error calling Account Service", e);
        }

    }

    private PaymentMethodDTO hideSensitivePaymentMethodInfo(PaymentMethodDTO paymentMethodDTO) {
        PaymentMethodDTO paymentMethodForOrder = new PaymentMethodDTO();
        paymentMethodForOrder.setPaymentMethodId(paymentMethodDTO.getPaymentMethodId());
        paymentMethodForOrder.setType(paymentMethodDTO.getType());
        String cardNumber = paymentMethodDTO.getCardNumber();
        if (cardNumber != null && cardNumber.length() > 4) {
            paymentMethodForOrder.setCardNumber("**** **** **** " + cardNumber.substring(cardNumber.length() - 4));
        }
        return paymentMethodForOrder;
    }

    private OrderDTO convertToDTO(Order order) {
        return new OrderDTO(order.getOrderId(), order.getUserId(), order.getOrderStatus(),
                order.getTotalAmount(), order.getCreatedAt(), order.getUpdatedAt(),
                order.getItems(), order.getPaymentStatus(), order.getShippingAddress(),
                order.getBillingAddress(), order.getPaymentMethod(), order.getTransactionKey(),
                order.getCurrency().getCurrencyCode(), order.getRefundStatus(), order.getRefundedAmount());

    }

    private OrderDTO convertToDTO(OrderByUser order) {
        return new OrderDTO(order.getOrderId(), order.getUserId(), order.getOrderStatus(),
                order.getTotalAmount(), order.getCreatedAt(), order.getUpdatedAt(),
                order.getItems(), order.getPaymentStatus(), order.getShippingAddress(),
                order.getBillingAddress(), order.getPaymentMethod(), order.getTransactionKey(),
                order.getCurrency().getCurrencyCode(), order.getRefundStatus(), order.getRefundedAmount());
    }

    private OrderByUser convertToOrderByUser(Order order) {
        return new OrderByUser(order.getUserId(), order.getCreatedAt(), order.getOrderId(),
                order.getOrderStatus(), order.getTotalAmount(), order.getUpdatedAt(),
                order.getItems(), order.getPaymentStatus(), order.getShippingAddress(),
                order.getBillingAddress(), order.getPaymentMethod(), order.getTransactionKey(),
                order.getCurrency().getCurrencyCode(), order.getRefundStatus(), order.getRefundedAmount());
    }

    private OrderEvent convertToOrderEvent(Order order, OrderEventType type) {
        return new OrderEvent(type,
                order.getUserId(), order.getOrderId(),
                order.getItems(), order.getShippingAddress(),
                order.getCreatedAt(), order.getUpdatedAt());
    }
}

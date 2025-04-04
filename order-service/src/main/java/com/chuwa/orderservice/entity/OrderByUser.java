package com.chuwa.orderservice.entity;

import com.chuwa.orderservice.enums.OrderStatus;
import com.chuwa.orderservice.enums.PaymentRefundStatus;
import com.chuwa.orderservice.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;

import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Table("orders_by_user")
public class OrderByUser {

    @PrimaryKeyColumn(name = "user_id", type = PARTITIONED)
    private UUID userId;

    @PrimaryKeyColumn(name = "created_at", type = CLUSTERED)
    private LocalDateTime createdAt;

    @Column("order_id")
    private UUID orderId;
    @Column("order_status")
    private OrderStatus orderStatus;

    @Column("total_amount")
    private BigDecimal totalAmount;
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("items")
    private String items;

    @Column("payment_status")
    private PaymentStatus paymentStatus;

    @Column("shipping_address")
    private String shippingAddress;  // Full address snapshot in JSON

    @Column("billing_address")
    private String billingAddress;  // Full address snapshot in JSON

    @Column("payment_method")
    private String paymentMethod; // only necessary metadata snapshot from the payment method

    @Column("transaction_key")
    private UUID transactionKey; //to ensure idempotency

    @Column("currency")
    private String currency;

    @Column("refund_status")
    private PaymentRefundStatus refundStatus;

    @Column("refunded_amount")
    private BigDecimal refundedAmount;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public UUID getTransactionKey() {
        return transactionKey;
    }

    public void setTransactionKey(UUID transactionKey) {
        this.transactionKey = transactionKey;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency.getCurrencyCode();
    }

    public Currency getCurrency() {
        return Currency.getInstance(this.currency);
    }

    public PaymentRefundStatus getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(PaymentRefundStatus refundStatus) {
        this.refundStatus = refundStatus;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }
}


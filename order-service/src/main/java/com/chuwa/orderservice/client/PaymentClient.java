package com.chuwa.orderservice.client;

import com.chuwa.orderservice.payload.RefundRequestDTO;
import com.chuwa.orderservice.payload.ValidatePaymentRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;


@FeignClient(name = "payment-service", path = "/api/v1/payments")
public interface PaymentClient {
    @PostMapping("/validate")
    Map<String, String> initiatePayment(@RequestBody ValidatePaymentRequestDTO validatePaymentRequestDTO);

    @PutMapping("/cancel")
    void cancelAuthorization(@RequestParam("transactionKey") UUID transactionKey);

    @PutMapping("/refund")
    void initiateRefund(@RequestBody RefundRequestDTO refundRequest);

}

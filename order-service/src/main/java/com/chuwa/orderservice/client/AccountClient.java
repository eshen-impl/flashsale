package com.chuwa.orderservice.client;

import com.chuwa.orderservice.config.SecuredFeignConfig;
import com.chuwa.orderservice.payload.AddressDTO;
import com.chuwa.orderservice.payload.PaymentMethodDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service", path = "/api/v1/user", configuration = SecuredFeignConfig.class)
public interface AccountClient {

    @GetMapping("/addresses")
    AddressDTO getAddress(@RequestParam("addressId") Long addressId);

    @GetMapping("/payment-methods")
    PaymentMethodDTO getPaymentMethod(@RequestParam("paymentMethodId") Long paymentMethodId);


}

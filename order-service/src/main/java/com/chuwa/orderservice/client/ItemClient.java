package com.chuwa.orderservice.client;

import com.chuwa.orderservice.config.NoAuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "item-service", path = "/api/v1/items", configuration = NoAuthFeignConfig.class)
public interface ItemClient {

//    @PostMapping("/availability")
//    Map<String, Integer> getAvailableUnits(@RequestBody List<String> itemIds);

    @PutMapping("/mgmt/flashsale/{id}/decrement-stock")
    Boolean decrementStock(@PathVariable("id") Long id);
}


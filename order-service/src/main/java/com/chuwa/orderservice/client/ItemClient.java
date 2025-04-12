package com.chuwa.orderservice.client;

import com.chuwa.orderservice.config.NoAuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "item-service", path = "/api/v1/items", configuration = NoAuthFeignConfig.class)
public interface ItemClient {

//    @PostMapping("/availability")
//    Map<String, Integer> getAvailableUnits(@RequestBody List<String> itemIds);

    @PutMapping("/mgmt/flashsale/{id}/decrement-stock")
    Boolean decrementStock(@PathVariable("id") Long id);

    @GetMapping("/mgmt/flashsale/{id}/stock")
    Integer getStock(@PathVariable("id") Long id);

    @PutMapping("/mgmt/flashsale/{id}/increment-stock")
    Boolean incrementStock(@PathVariable("id") Long id);
}


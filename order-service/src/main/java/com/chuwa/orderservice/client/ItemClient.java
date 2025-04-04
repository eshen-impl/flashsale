package com.chuwa.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "item-service", path = "/api/v1/items")
public interface ItemClient {

    @PostMapping("/availability")
    Map<String, Integer> getAvailableUnits(@RequestBody List<String> itemIds);
}


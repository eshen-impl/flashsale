package com.chuwa.orderservice.util;
import com.chuwa.orderservice.payload.CartItem;
import com.chuwa.orderservice.payload.FlashSaleItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing " + object.getClass() +" to JSON", e);
        }
    }

    public static List<CartItem> fromJsonToCartItemList(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON to CartItem list", e);
        }
    }

    public static CartItem fromJsonToCartItem(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, CartItem.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing CartItem", e);
        }
    }

    public static FlashSaleItem fromJsonToFlashSaleItem(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, FlashSaleItem.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing FlashSaleItem", e);
        }
    }
}

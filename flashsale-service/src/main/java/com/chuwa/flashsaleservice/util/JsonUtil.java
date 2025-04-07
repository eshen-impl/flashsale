package com.chuwa.flashsaleservice.util;

import com.chuwa.flashsaleservice.payload.FlashSaleItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing " + object.getClass() +" to JSON", e);
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

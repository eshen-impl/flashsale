package com.chuwa.flashsaleservice.util;

import com.chuwa.flashsaleservice.payload.FlashSaleItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;


public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Register JavaTimeModule to handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule());
        // Disable writing dates as timestamps (i.e., use ISO-8601 format)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

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
            System.out.println(e.getMessage());
            throw new RuntimeException("Error deserializing FlashSaleItem", e);
        }
    }
}

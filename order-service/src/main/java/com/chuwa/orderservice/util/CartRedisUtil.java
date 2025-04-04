package com.chuwa.orderservice.util;


import com.chuwa.orderservice.payload.CartItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class CartRedisUtil {

    private final HashOperations<String, String, String> hashOperations;

    public CartRedisUtil(@Qualifier("cartRedisTemplate") RedisTemplate<String, Object> cartRedisTemplate) {
        this.hashOperations = cartRedisTemplate.opsForHash();
    }

    public List<CartItem> getCartItems(String cartKey) {
        Map<String, String> cartMap = hashOperations.entries(cartKey);

        List<CartItem> cartItems = new ArrayList<>();

        // Iterate over the values of the map and deserialize each JSON string into a CartItem
        for (String json : cartMap.values()) {
            CartItem cartItem = JsonUtil.fromJsonToCartItem(json);  // Deserialize the JSON string into CartItem
            cartItems.add(cartItem);
        }

        return cartItems;
    }

    public void clearCart(String cartKey) {
        hashOperations.getOperations().delete(cartKey);
    }

}

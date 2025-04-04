package com.chuwa.orderservice.util;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

/**
 * encode UUIDs to Base64 for shorter keys
 */
public class UUIDUtil {

    public static String encodeUUID(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
    }

    public static UUID decodeUUID(String base64UUID) {
        byte[] bytes = Base64.getUrlDecoder().decode(base64UUID);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long high = buffer.getLong();
        long low = buffer.getLong();
        return new UUID(high, low);
    }
}

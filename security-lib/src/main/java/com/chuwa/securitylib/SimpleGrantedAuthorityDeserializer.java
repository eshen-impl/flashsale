package com.chuwa.securitylib;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;

public class SimpleGrantedAuthorityDeserializer extends JsonDeserializer<SimpleGrantedAuthority> {
    @Override
    public SimpleGrantedAuthority deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectNode node = p.getCodec().readTree(p);
        return new SimpleGrantedAuthority(node.get("authority").asText());
    }
}

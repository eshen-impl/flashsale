package com.chuwa.securitylib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomObjectMapperProvider {
    public static ObjectMapper create() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(SimpleGrantedAuthority.class, new SimpleGrantedAuthorityDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}

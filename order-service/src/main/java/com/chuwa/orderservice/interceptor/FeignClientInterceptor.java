package com.chuwa.orderservice.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class FeignClientInterceptor implements RequestInterceptor  {

    private final HttpServletRequest request;

    public FeignClientInterceptor(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void apply(RequestTemplate template) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            template.header("Authorization", token);  // Forward the JWT token
        }
    }


}

package com.chuwa.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyCartException extends RuntimeException {
    public EmptyCartException(String message) {
        super(message);
    }
}
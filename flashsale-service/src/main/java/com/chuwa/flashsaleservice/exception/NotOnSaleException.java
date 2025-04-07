package com.chuwa.flashsaleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotOnSaleException extends RuntimeException {
    public NotOnSaleException(String message) {
        super(message);
    }
}
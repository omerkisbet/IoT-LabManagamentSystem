package com.example.demo.exception;

public class EmailDeliveryException extends RuntimeException {

    public EmailDeliveryException(String message) {
        super(message);
    }

    public EmailDeliveryException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}

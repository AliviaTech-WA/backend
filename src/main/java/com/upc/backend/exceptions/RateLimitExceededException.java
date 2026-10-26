package com.upc.backend.exceptions;

// HU05 | END04: indica que se superó el límite temporal por correo o dirección IP.
public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}

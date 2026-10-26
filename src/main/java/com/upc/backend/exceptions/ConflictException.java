package com.upc.backend.exceptions;

// Infraestructura común: representa conflictos de estado o duplicidad y se traduce a HTTP 409.
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}

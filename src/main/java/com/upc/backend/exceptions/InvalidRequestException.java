package com.upc.backend.exceptions;

// Infraestructura común: representa solicitudes inválidas y se traduce a HTTP 400.
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}

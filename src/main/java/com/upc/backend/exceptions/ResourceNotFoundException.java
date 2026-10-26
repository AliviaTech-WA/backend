package com.upc.backend.exceptions;

// Infraestructura común: representa recursos inexistentes y se traduce a HTTP 404.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

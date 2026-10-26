package com.upc.backend.exceptions;

// HU31 | END25: informa que el proveedor de IA no pudo generar un resultado persistible.
public class AnalisisIaNoDisponibleException extends RuntimeException {
    public AnalisisIaNoDisponibleException(String message) {
        super(message);
    }

    public AnalisisIaNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}

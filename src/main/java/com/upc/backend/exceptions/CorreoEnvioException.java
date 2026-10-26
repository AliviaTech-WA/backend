package com.upc.backend.exceptions;

// HU02 y HU05: informa que una invitación o recuperación no pudo enviarse por SMTP.
public class CorreoEnvioException extends RuntimeException {

    public CorreoEnvioException(String message) {
        super(message);
    }

    public CorreoEnvioException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.upc.backend.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
// Infraestructura común: normaliza errores 400, 401, 404, 409, 429, 503 y 500 para todos los endpoints.
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CONFLICT", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CONFLICT", "La operación entra en conflicto con los datos existentes."));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("UNAUTHORIZED", "Credenciales inválidas."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    // HU05 | END05-END06: traduce UUID o JSON mal formados a una solicitud inválida.
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_REQUEST", "El cuerpo de la solicitud no tiene un formato válido."));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    // HU05 | END04: informa los límites por correo e IP con HTTP 429.
    public ResponseEntity<ErrorResponse> handleRateLimit(RateLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorResponse("RATE_LIMIT_EXCEEDED", ex.getMessage()));
    }

    @ExceptionHandler(CorreoEnvioException.class)
    // HU02 | END24: evita afirmar que la invitación fue enviada cuando SMTP falla.
    public ResponseEntity<ErrorResponse> handleCorreoEnvio(CorreoEnvioException ex) {
        log.warn("Mail delivery failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("MAIL_UNAVAILABLE", ex.getMessage()));
    }

    @ExceptionHandler(AnalisisIaNoDisponibleException.class)
    // HU31 | END25: evita guardar o devolver como exitoso un análisis si Gemini no responde correctamente.
    public ResponseEntity<ErrorResponse> handleAnalisisIaNoDisponible(AnalisisIaNoDisponibleException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(
                        "AI_UNAVAILABLE",
                        "No fue posible generar el análisis en este momento."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Ocurrió un error inesperado."));
    }

    public record ErrorResponse(String code, String message) {
    }
}

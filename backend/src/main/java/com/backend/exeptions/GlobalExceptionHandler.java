package com.backend.exeptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Map<String, Object>> handleEmailUsed(EmailAlreadyUsedException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(AccountNotConfirmedException.class)
    public ResponseEntity<Map<String, Object>> handleNotConfirmed(AccountNotConfirmedException e) {
        return buildResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler({InvalidTokenException.class, RefreshTokenInvalidException.class})
    public ResponseEntity<Map<String, Object>> handleInvalidToken(RuntimeException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredToken(ExpiredTokenException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception e) {
        log.error("Unexpected error occurred", e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}

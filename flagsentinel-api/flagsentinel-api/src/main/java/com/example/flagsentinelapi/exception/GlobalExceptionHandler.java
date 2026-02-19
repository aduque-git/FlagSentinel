package com.example.flagsentinelapi.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // -----------------------------
    // 1) Excepciones de negocio
    // -----------------------------
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {

        HttpStatus status = switch (ex.getCode()) {
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "CONFLICT" -> HttpStatus.CONFLICT;
            case "BAD_REQUEST" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };

        return ResponseEntity.status(status).body(
                new ErrorResponse(ex.getCode(), ex.getMessage(), Instant.now())
        );
    }

    // -----------------------------
    // 2) Errores de integridad BD
    // -----------------------------
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {

        String message = extractMeaningfulMessage(ex);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse("DATA_INTEGRITY_ERROR", message, Instant.now())
        );
    }

    // -----------------------------
    // 3) Errores inesperados
    // -----------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("INTERNAL_ERROR", "Unexpected server error", Instant.now())
        );
    }

    // -----------------------------
    // 4) Parser inteligente de errores BD
    // -----------------------------
    private String extractMeaningfulMessage(DataIntegrityViolationException ex) {

        Throwable root = ex.getRootCause();
        if (root == null) return "Database integrity error";

        String msg = root.getMessage().toLowerCase();

        // -----------------------------
        // DUPLICADOS (UNIQUE)
        // -----------------------------
        if (msg.contains("unique") || msg.contains("duplicate")) {

            if (msg.contains("username")) {
                return "Username already exists";
            }

            if (msg.contains("flag_code")) {
                return "Feature flag code already exists";
            }

            // Si en el futuro añades unique a rules:
            if (msg.contains("rule_code")) {
                return "Rule code already exists";
            }

            return "Duplicate value";
        }

        // -----------------------------
        // FOREIGN KEY (FK rotas)
        // -----------------------------
        if (msg.contains("foreign key") || msg.contains("fk_")) {

            if (msg.contains("feature_flag_id")) {
                return "Feature flag does not exist";
            }

            if (msg.contains("rule_id")) {
                return "Rule does not exist";
            }

            return "Referenced entity does not exist";
        }

        // -----------------------------
        // NOT NULL
        // -----------------------------
        if (msg.contains("not-null") || msg.contains("null value")) {
            return "A required field is missing";
        }

        return "Database integrity error";
    }

    // 401 - Token inválido o no enviado
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse("UNAUTHORIZED", "Authentication required or invalid token", Instant.now())
        );
    }

    // 403 - Usuario autenticado pero sin permisos
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErrorResponse("FORBIDDEN", "You do not have permission to perform this action", Instant.now())
        );
    }

    // JWT expirado
    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(io.jsonwebtoken.ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse("TOKEN_EXPIRED", "Your session has expired", Instant.now())
        );
    }

    // JWT inválido
    @ExceptionHandler(io.jsonwebtoken.SignatureException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJwt(io.jsonwebtoken.SignatureException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse("INVALID_TOKEN", "Invalid authentication token", Instant.now())
        );
    }

}

package com.example.flagsentinelapi.exception;
import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =========================
    // BUSINESS EXCEPTIONS
    // =========================
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex,
                                                            HttpServletRequest request) {

        log.warn(ApiLogMessages.get(
                LogPropertiesKeys.BUSINESS_EXCEPTION,
                ex.getCode(),
                request.getRequestURI(),
                MDC.get("traceId")
        ));

        return buildResponse(ex.getCode(), ex.getMessage(), request);
    }

    // =========================
    // DATABASE ERRORS
    // =========================
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDatabase(DataIntegrityViolationException ex,
                                                        HttpServletRequest request) {

        log.error(ApiLogMessages.get(
                LogPropertiesKeys.DATABASE_EXCEPTION,
                request.getRequestURI(),
                MDC.get("traceId")
        ), ex);

        return buildResponse(
                ErrorCode.DATABASE_ERROR,
                "Database constraint violation",
                request
        );
    }

    // =========================
    // SECURITY
    // =========================
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex,
                                                    HttpServletRequest request) {

        log.warn(ApiLogMessages.get(
                LogPropertiesKeys.AUTH_EXCEPTION,
                request.getRequestURI(),
                MDC.get("traceId")
        ));

        return buildResponse(
                ErrorCode.UNAUTHORIZED,
                "Authentication required",
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex,
                                                         HttpServletRequest request) {

        log.warn(ApiLogMessages.get(
                LogPropertiesKeys.ACCESS_DENIED_EXCEPTION,
                request.getRequestURI(),
                MDC.get("traceId")
        ));

        return buildResponse(
                ErrorCode.FORBIDDEN,
                "Insufficient permissions",
                request
        );
    }

    // =========================
    // FALLBACK
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex,
                                                          HttpServletRequest request) {

        log.error(ApiLogMessages.get(
                LogPropertiesKeys.UNEXPECTED_EXCEPTION,
                request.getRequestURI(),
                MDC.get("traceId")
        ), ex);

        return buildResponse(
                ErrorCode.INTERNAL_ERROR,
                "Unexpected server error",
                request
        );
    }

    // =========================
    // BUILDER
    // =========================
    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode code,
                                                        String message,
                                                        HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                code.name(),
                message,
                request.getRequestURI(),
                MDC.get("traceId"),
                Instant.now()
        );

        return ResponseEntity.status(code.getStatus()).body(response);
    }
}
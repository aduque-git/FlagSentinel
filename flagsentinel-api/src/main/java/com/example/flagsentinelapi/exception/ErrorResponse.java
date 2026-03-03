package com.example.flagsentinelapi.exception;

import java.time.Instant;


public record ErrorResponse(
        String code,
        String message,
        String path,
        String traceId,
        Instant timestamp
) {}

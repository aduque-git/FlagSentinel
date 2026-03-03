package com.example.flagsentinelapi.exception;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(ErrorCode.VALIDATION_ERROR, message);
    }
}

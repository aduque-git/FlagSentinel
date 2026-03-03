package com.example.flagsentinelapi.exception;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
}

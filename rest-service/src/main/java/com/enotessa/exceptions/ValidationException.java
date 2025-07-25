package com.enotessa.exceptions;

import org.springframework.http.HttpStatus;

public class ValidationException extends ClientRequestException {
    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
    }
}

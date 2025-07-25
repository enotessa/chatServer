package com.enotessa.exceptions;

import org.springframework.http.HttpStatus;

public class RegisterException extends ClientRequestException {
    public RegisterException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
    }
}

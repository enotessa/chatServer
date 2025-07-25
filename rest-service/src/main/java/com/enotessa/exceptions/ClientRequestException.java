package com.enotessa.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClientRequestException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorCode;

    public ClientRequestException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}

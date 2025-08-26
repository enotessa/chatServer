package com.enotessa.gpt.exceptions;

public class GptApiException extends RuntimeException {
    public GptApiException(String message) {
        super(message);
    }

    public GptApiException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

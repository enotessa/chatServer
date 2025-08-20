package com.enotessa.exceptions;

public class GptServiceException extends RuntimeException {
  public GptServiceException(String message, Throwable cause) {
    super(message, cause);
  }}

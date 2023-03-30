package com.kourchenko.exception;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -4834752686758321850L;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

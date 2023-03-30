package com.kourchenko.exception;

public class AmazonException extends RuntimeException {

    private static final long serialVersionUID = -4834752686758321850L;

    public AmazonException(String message) {
        super(message);
    }

    public AmazonException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

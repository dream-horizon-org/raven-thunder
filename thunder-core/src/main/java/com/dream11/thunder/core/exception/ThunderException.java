package com.dream11.thunder.core.exception;

import lombok.Getter;

/**
 * Base exception class for Thunder application
 */
@Getter
public class ThunderException extends RuntimeException {
    private final String errorCode;
    private final int httpStatusCode;

    public ThunderException(String message, String errorCode, int httpStatusCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }

    public ThunderException(String message, String errorCode, int httpStatusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }
}


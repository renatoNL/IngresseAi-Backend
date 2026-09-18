package com.ingressos.exception;

import org.springframework.http.HttpStatus;

public class IntegracaoIaException extends IllegalStateException {
    private final HttpStatus status;

    public IntegracaoIaException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public IntegracaoIaException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

package org.zhvtsv.exception;

import jakarta.ws.rs.core.Response;

public abstract class HttpStatusException extends RuntimeException {
    public HttpStatusException(String message) {
        super(message);
    }
    public abstract Response.Status getHttpStatus();
}

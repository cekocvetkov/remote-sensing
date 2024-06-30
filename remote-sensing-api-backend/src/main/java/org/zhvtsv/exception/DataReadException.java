package org.zhvtsv.exception;

import jakarta.ws.rs.core.Response;

public class DataReadException extends HttpStatusException {
    public DataReadException(String message) {
        super(message);
    }

    @Override
    public Response.Status getHttpStatus() {
        return Response.Status.INTERNAL_SERVER_ERROR;
    }
}

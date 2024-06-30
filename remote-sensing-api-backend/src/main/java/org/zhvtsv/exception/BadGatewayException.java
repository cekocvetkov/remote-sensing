package org.zhvtsv.exception;

import jakarta.ws.rs.core.Response;

public class BadGatewayException extends HttpStatusException{
    public BadGatewayException(String message) {
        super(message);
    }

    @Override
    public Response.Status getHttpStatus() {
        return Response.Status.BAD_GATEWAY;
    }
}

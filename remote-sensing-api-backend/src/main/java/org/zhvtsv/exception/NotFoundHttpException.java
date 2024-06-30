package org.zhvtsv.exception;

import jakarta.ws.rs.core.Response;

public class NotFoundHttpException extends HttpStatusException {

    public NotFoundHttpException(String message) {
        super(message);
    }

    @Override
    public Response.Status getHttpStatus() {
        return Response.Status.NOT_FOUND;
    }
}

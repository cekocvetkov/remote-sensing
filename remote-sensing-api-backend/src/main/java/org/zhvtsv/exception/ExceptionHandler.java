package org.zhvtsv.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ExceptionMapper;

@Provider
public class ExceptionHandler
        implements ExceptionMapper<HttpStatusException> {

    @Override
    public Response toResponse(HttpStatusException e) {
        return Response.status(e.getHttpStatus().getStatusCode(), e.getMessage()).entity(e.getMessage()).build();
    }
}
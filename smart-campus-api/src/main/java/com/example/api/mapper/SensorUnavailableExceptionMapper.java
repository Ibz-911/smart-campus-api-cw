package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.ErrorResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        ErrorResponse error = new ErrorResponse(
                403,
                "Forbidden",
                ex.getMessage(),
                uriInfo != null ? uriInfo.getPath() : "unknown",
                System.currentTimeMillis()
        );

        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .build();
    }
}
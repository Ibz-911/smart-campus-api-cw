package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.ErrorResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                422,
                "Unprocessable Entity",
                ex.getMessage(),
                uriInfo != null ? uriInfo.getPath() : "unknown",
                System.currentTimeMillis()
        );

        return Response.status(422)
                .entity(error)
                .build();
    }
}

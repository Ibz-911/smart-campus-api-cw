package com.smartcampus.mapper;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.ErrorResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        ErrorResponse error = new ErrorResponse(
                409,
                "Conflict",
                ex.getMessage(),
                uriInfo != null ? uriInfo.getPath() : "unknown",
                System.currentTimeMillis()
        );

        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .build();
    }
}

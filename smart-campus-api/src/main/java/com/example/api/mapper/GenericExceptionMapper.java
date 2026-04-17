package com.smartcampus.mapper;

import com.smartcampus.model.ErrorResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable ex) {
        if (ex instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) ex;
            int status = webEx.getResponse().getStatus();

            ErrorResponse error = new ErrorResponse(
                    status,
                    "Request Error",
                    ex.getMessage(),
                    uriInfo != null ? uriInfo.getPath() : "unknown",
                    System.currentTimeMillis()
            );

            return Response.status(status)
                    .entity(error)
                    .build();
        }

        LOGGER.log(Level.SEVERE, "Unexpected server error", ex);

        ErrorResponse error = new ErrorResponse(
                500,
                "Internal Server Error",
                "An unexpected error occurred. Please contact the API administrator.",
                uriInfo != null ? uriInfo.getPath() : "unknown",
                System.currentTimeMillis()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }
}
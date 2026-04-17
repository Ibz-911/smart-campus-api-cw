package com.smartcampus.resource;

import com.smartcampus.data.DataStore;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    public RoomResource() {
        DataStore.seedData();
    }

    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(DataStore.ROOMS.values());
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null || room.getId() == null || room.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room id is required")
                    .build();
        }

        if (DataStore.ROOMS.containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Room with id " + room.getId() + " already exists")
                    .build();
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        DataStore.ROOMS.put(room.getId(), room);

        URI uri = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(uri).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Room getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.ROOMS.get(roomId);
        if (room == null) {
            throw new NotFoundException("Room not found: " + roomId);
        }
        return room;
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.ROOMS.get(roomId);

        if (room == null) {
            throw new NotFoundException("Room not found: " + roomId);
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                    "Room " + roomId + " cannot be deleted because sensors are still assigned to it."
            );
        }

        DataStore.ROOMS.remove(roomId);

        return Response.ok()
                .entity("{\"message\":\"Room deleted successfully\"}")
                .build();
    }
}
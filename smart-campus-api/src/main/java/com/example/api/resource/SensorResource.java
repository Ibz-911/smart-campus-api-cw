package com.smartcampus.resource;

import com.smartcampus.data.DataStore;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> sensors = new ArrayList<>(DataStore.SENSORS.values());

        if (type != null && !type.isBlank()) {
            return sensors.stream()
                    .filter(sensor -> sensor.getType() != null
                            && sensor.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        return sensors;
    }

    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null || sensor.getId() == null || sensor.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sensor id is required")
                    .build();
        }

        if (DataStore.SENSORS.containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Sensor with id " + sensor.getId() + " already exists")
                    .build();
        }

        Room room = DataStore.ROOMS.get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException(
                    "Cannot create sensor because roomId '" + sensor.getRoomId() + "' does not exist."
            );
        }

        DataStore.SENSORS.put(sensor.getId(), sensor);

        if (!room.getSensorIds().contains(sensor.getId())) {
            room.getSensorIds().add(sensor.getId());
        }

        URI uri = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(uri).entity(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        if (!DataStore.SENSORS.containsKey(sensorId)) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }
        return new SensorReadingResource(sensorId);
    }
}

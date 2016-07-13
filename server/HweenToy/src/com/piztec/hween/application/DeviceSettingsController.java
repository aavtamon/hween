package com.piztec.hween.application;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.http.OPTIONS;
import org.json.JSONObject;

@Path("device")
public class DeviceSettingsController {
	@OPTIONS
	@Path("{deviceType}/settings")
	public Response getDeviceSettingsOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	@GET
	@Path("{deviceType}/settings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceSettings(@PathParam("deviceType") String deviceType, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		String settings = "{\"categories\": [ {\"data\": \"fun\", \"display\": \"Fun\"}, {\"data\": \"scary\", \"display\": \"Scary\"} ],";
		
		settings += "\"supportedCommands\": [ {\"data\": \"reset\", \"display\": \"Reset\", \"description\": \"Sets toy to the initial position\"},";
		settings += "{\"data\": \"move_up\", \"display\": \"Move Up\", \"description\": \"Move toy up one inch\"},";
		settings += "{\"data\": \"move_down\", \"display\": \"Move Down\", \"description\": \"Move toy down one inch\"},";
		settings += "{\"data\": \"turn_left\", \"display\": \"Turn Left\", \"description\": \"Turn left a bit\"},";
		settings += "{\"data\": \"turn_right\", \"display\": \"Turn Right\", \"description\": \"Turn right a bit\"},";
		settings += "{\"data\": \"eyes_on\", \"display\": \"Turn Eyes On\", \"description\": \"Turn eyes on\"},";
		settings += "{\"data\": \"eyes_off\", \"display\": \"Turn Eyes Off\", \"description\": \"Turn eyes off\"},";
		settings += "{\"data\": \"talk\", \"display\": \"Speak\", \"description\": \"Say something\"},";
		settings += "{\"data\": \"pause\", \"display\": \"Do nothing\", \"description\": \"Do nothing for a bit\"} ],";
		
		settings += "\"supportedProgramTriggers\": [ {\"data\": \"immediately\", \"display\": \"Previous\"},";
		settings += "{\"data\": \"delay\", \"display\": \"Delay\"},";
		settings += "{\"data\": \"motion\", \"display\": \"Motion\"} ]}";
		
		return ControllerUtils.buildResponse(Response.Status.OK, settings);
	}
}

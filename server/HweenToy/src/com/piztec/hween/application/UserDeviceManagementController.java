package com.piztec.hween.application;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.http.OPTIONS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Path("devices/user")
public class UserDeviceManagementController {
	private JSONObject deviceIds;
	
	public UserDeviceManagementController() {
		deviceIds = new JSONObject();
		try {
			JSONArray registered = new JSONArray();
			registered.put(1000);
			deviceIds.put("registered", registered);
			
			JSONArray unregistered = new JSONArray();
			unregistered.put(2000);
			deviceIds.put("unregistered", unregistered);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	@OPTIONS
	@Path("{userId}/devices")
	public Response getDeviceIdsOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	@GET
	@Path("{userId}/devices")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceIds(@PathParam("userId") int userId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		return ControllerUtils.buildResponse(Response.Status.OK, deviceIds);
	}
	@PUT
	@Path("{userId}/devices")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerDevices(String idsText, @PathParam("userId") int userId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		try {
			JSONObject ids = new JSONObject(idsText);
			JSONArray newIds = ids.getJSONArray("ids");
			
			JSONArray registeredIds = deviceIds.getJSONArray("registered");
			for (int i = 0; i < newIds.length(); i++) {
				int newId = newIds.getInt(i);
				registeredIds.put(newId);
			}
			deviceIds.put("registered", registeredIds);

			return ControllerUtils.buildResponse(Response.Status.OK, deviceIds);
		} catch (Exception e) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST, e.getMessage());
		}
	}
	
	
	@OPTIONS
	@Path("{userId}/device/{deviceId}")
	public Response getDeviceInfoOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	@GET
	@Path("{userId}/device/{deviceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceInfo(@PathParam("userId") int userId, @PathParam("deviceId") int deviceId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		String info = "{\"id\": " + deviceId + ", \"type\": \"stump_ghost\", \"version\": \"1.0\", \"name\": \"Ghost-" + deviceId + "\", \"icon\": null, \"serial_number\": \"000000" + deviceId + "\", \"status\": \"connected\", \"ip_address\": \"192.168.0.100\"}";
		
		return ControllerUtils.buildResponse(Response.Status.OK, info);
	}
}

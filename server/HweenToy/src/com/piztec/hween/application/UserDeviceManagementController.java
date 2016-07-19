package com.piztec.hween.application;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.http.OPTIONS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.piztec.hween.persistance.StorageManager;

@Path("devices/user")
public class UserDeviceManagementController {
	private JSONObject deviceIds;
	
	public UserDeviceManagementController() {
		StorageManager.getInstance();
		
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
			JSONArray registeredIds = deviceIds.getJSONArray("registered");
			JSONArray unregisteredIds = deviceIds.getJSONArray("unregistered");

			JSONObject ids = new JSONObject(idsText);
			JSONArray newIds = ids.getJSONArray("ids");
			for (int i = 0; i < newIds.length(); i++) {
				int newId = newIds.getInt(i);
				registeredIds.put(newId);
				
				unregisteredIds = ControllerUtils.removeFromArray(unregisteredIds, newId);
			}
			deviceIds.put("registered", registeredIds);
			deviceIds.put("unregistered", unregisteredIds);

			return ControllerUtils.buildResponse(Response.Status.OK, deviceIds);
		} catch (Exception e) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST, e.getMessage());
		}
	}
	
	@OPTIONS
	@Path("{userId}/devices/{deviceId}")
	public Response unregisterDeviceOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}

	@DELETE
	@Path("{userId}/devices/{deviceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response unregisterDevice(@PathParam("userId") int userId, @PathParam("deviceId") int deviceId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		try {
			JSONArray registeredIds = deviceIds.getJSONArray("registered");
			JSONArray unregisteredIds = deviceIds.getJSONArray("unregistered");
			
			registeredIds = ControllerUtils.removeFromArray(registeredIds, deviceId);
			unregisteredIds.put(deviceId);

			deviceIds.put("registered", registeredIds);
			deviceIds.put("unregistered", unregisteredIds);
			
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

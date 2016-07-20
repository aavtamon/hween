package com.piztec.hween.application;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.http.OPTIONS;
import org.json.JSONArray;
import org.json.JSONObject;

import com.piztec.hween.persistance.StorageManager;

@Path("devices/user")
public class UserDeviceManagementController {
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

		return ControllerUtils.buildResponse(Response.Status.OK, StorageManager.getInstance().getUserDevicesManager().getDeviceIds());
	}
	
	@POST
	@Path("{userId}/devices")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addDevice(String body, @PathParam("userId") int userId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		try {
			JSONObject bodyObject = new JSONObject(body);
			String serialNumber = bodyObject.getString("serial_number");
			int verificationCode = bodyObject.getInt("verification_code");
			
			JSONObject registryInfo = StorageManager.getInstance().getDeviceRegistryManager().getDeviceInfo(serialNumber);
			if (registryInfo == null) {
				return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
			}
			
			int deviceVerificationCode = registryInfo.getInt("verification_code");

			if (deviceVerificationCode != verificationCode) {
				return ControllerUtils.buildResponse(Response.Status.FORBIDDEN);
			}
			
			int deviceId = StorageManager.getInstance().getUserDevicesManager().addDevice(serialNumber);
			if (deviceId != -1) {
				return ControllerUtils.buildResponse(Response.Status.OK, StorageManager.getInstance().getUserDevicesManager().getDeviceIds());
			} else {
				return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
			}
		} catch(Exception e) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST, e.getMessage());
		}
	}
	
	

	@OPTIONS
	@Path("{userId}/devices/{deviceId}")
	public Response deviceOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}

	@PUT
	@Path("{userId}/devices/{deviceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateDevice(String body, @PathParam("userId") int userId, @PathParam("deviceId") int deviceId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		try {
			JSONObject bodyObject = new JSONObject(body);
			
			boolean registered = bodyObject.getBoolean("registered");
			if (registered) {
				StorageManager.getInstance().getUserDevicesManager().registerDevice(deviceId);
			} else {
				StorageManager.getInstance().getUserDevicesManager().unregisterDevice(deviceId);
			}
			
			return ControllerUtils.buildResponse(Response.Status.OK, StorageManager.getInstance().getUserDevicesManager().getDeviceIds());
		} catch (Exception e) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST, e.getMessage());
		}
	}
	
	@DELETE
	@Path("{userId}/devices/{deviceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response unregisterDevice(@PathParam("userId") int userId, @PathParam("deviceId") int deviceId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		StorageManager.getInstance().getUserDevicesManager().removeDevice(deviceId);
		return ControllerUtils.buildResponse(Response.Status.OK, StorageManager.getInstance().getUserDevicesManager().getDeviceIds());
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
		
		JSONObject deviceInfo = StorageManager.getInstance().getUserDevicesManager().getDeviceInfo(deviceId);
		if (deviceInfo != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, deviceInfo);
		} else {
			return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
		}
	}
}

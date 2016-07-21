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
import org.json.JSONException;
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
	
	@GET
	@Path("{userId}/devices/{deviceId}")
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
	public Response removeDevice(@PathParam("userId") int userId, @PathParam("deviceId") int deviceId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		StorageManager.getInstance().getUserDevicesManager().removeDevice(deviceId);
		return ControllerUtils.buildResponse(Response.Status.OK, StorageManager.getInstance().getUserDevicesManager().getDeviceIds());
	}
	
	
	

	@OPTIONS
	@Path("{userId}/devices/{deviceId}/schedule")
	public Response getDeviceScheduleOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	@GET
	@Path("{userId}/devices/{deviceId}/schedule")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceSchedule(@PathParam("userId") int userId, @PathParam("deviceId") int deviceId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		JSONObject deviceSchedule = StorageManager.getInstance().getUserDevicesManager().getDeviceSchedule(deviceId);
		if (deviceSchedule != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, deviceSchedule);
		} else {
			return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
		}
	}
	@PUT
	@Path("{userId}/devices/{deviceId}/schedule")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setDeviceSchedule(String body, @PathParam("userId") int userId, @PathParam("deviceId") int deviceId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		try {
			JSONObject deviceSchedule = new JSONObject(body);
			JSONObject schedule = StorageManager.getInstance().getUserDevicesManager().setDeviceSchedule(deviceId, deviceSchedule);
			if (schedule != null) {
				return ControllerUtils.buildResponse(Response.Status.OK, schedule);
			} else {
				return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
			}
		} catch (JSONException e) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
		}
	}


	@OPTIONS
	@Path("{userId}/devices/{deviceId}/mode")
	public Response getDeviceModeOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	@GET
	@Path("{userId}/devices/{deviceId}/mode")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceMode(@PathParam("userId") int userId, @PathParam("deviceId") int deviceId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		String mode = StorageManager.getInstance().getUserDevicesManager().getDeviceMode(deviceId);
		if (mode != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, "{\"mode\": \"" + mode + "\"}");
		} else {
			return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
		}
	}
	@PUT
	@Path("{userId}/devices/{deviceId}/mode")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setDeviceMode(String body, @PathParam("userId") int userId, @PathParam("deviceId") int deviceId, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		try {
			JSONObject deviceMode = new JSONObject(body);
			String mode = StorageManager.getInstance().getUserDevicesManager().setDeviceMode(deviceId, deviceMode.getString("mode"));
			if (mode != null) {
				return ControllerUtils.buildResponse(Response.Status.OK, "{\"mode\": \"" + mode + "\"}");
			} else {
				return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
			}
		} catch (JSONException e) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
		}
	}
}

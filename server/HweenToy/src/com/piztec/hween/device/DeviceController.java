package com.piztec.hween.device;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.http.OPTIONS;
import org.json.JSONException;
import org.json.JSONObject;

import com.piztec.hween.ControllerUtils;
import com.piztec.hween.persistance.StorageManager;

@Path("device")
public class DeviceController {
	@OPTIONS
	@Path("{serialNumber}")
	public Response deviceOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}

	@GET
	@Path("{serialNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceInfo(@PathParam("serialNumber") String serialNumber, @HeaderParam("Secret") String authHeader) {
		if (!DeviceUtils.isAuthenticated(serialNumber, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}

		return ControllerUtils.buildResponse(Response.Status.OK, StorageManager.getInstance().getDevicesManager().getDeviceInfo(serialNumber));
	}

	@PUT
	@Path("{serialNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setDeviceInfo(String body, @PathParam("serialNumber") String serialNumber, @HeaderParam("Secret") String authHeader) {
		if (!DeviceUtils.isAuthenticated(serialNumber, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		try {
			JSONObject bodyObject = new JSONObject(body);
			StorageManager.getInstance().getDevicesManager().reportDevice(serialNumber, bodyObject);
			
			return ControllerUtils.buildResponse(Response.Status.OK, StorageManager.getInstance().getDevicesManager().getDeviceInfo(serialNumber));
		} catch (JSONException e) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
		}
	}
}

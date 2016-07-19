package com.piztec.hween.application;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.http.OPTIONS;
import org.json.JSONObject;

import com.piztec.hween.persistance.StorageManager;

@Path("settings/device")
public class DeviceSettingsController {
	@OPTIONS
	@Path("{deviceType}")
	public Response getDeviceSettingsOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	@GET
	@Path("{deviceType}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceSettings(@PathParam("deviceType") String deviceType, @HeaderParam("Token") String authHeader) {
		if (!ControllerUtils.isAuthenticated(authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		JSONObject ds = StorageManager.getInstance().getDeviceSettingsManager().getDeviceSettings(deviceType);
		if (ds != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, ds);
		} else {
			return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
		}
	}
}

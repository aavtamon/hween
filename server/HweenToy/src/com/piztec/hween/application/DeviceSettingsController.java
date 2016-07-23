package com.piztec.hween.application;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.http.OPTIONS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.piztec.hween.ControllerUtils;
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
		if (!ApplicationUtils.isAuthenticated(authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		JSONObject ds = StorageManager.getInstance().getDeviceRegistryManager().getDeviceSettings(deviceType);
		if (ds != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, ds);
		} else {
			return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
		}
	}
	
	@OPTIONS
	@Path("{deviceType}/programs")
	public Response getStockProgramsOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	@GET
	@Path("{deviceType}/programs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStockPrograms(@PathParam("deviceType") String deviceType, @HeaderParam("Token") String authHeader) {
		if (!ApplicationUtils.isAuthenticated(authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		JSONObject stockPrograms = StorageManager.getInstance().getDeviceRegistryManager().getStockPrograms(deviceType);
		if (stockPrograms != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, stockPrograms);
		} else {
			return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
		}
	}
	@POST
	@Path("{deviceType}/programs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addStockProgram(String body, @PathParam("deviceType") String deviceType, @HeaderParam("Token") String authHeader) {
		if (!ApplicationUtils.isAuthenticated(authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		try {
			JSONObject program = new JSONObject(body);
			
			JSONObject stockPrograms = StorageManager.getInstance().getDeviceRegistryManager().addStockProgram(deviceType, program);
			if (stockPrograms != null) {
				return ControllerUtils.buildResponse(Response.Status.OK, stockPrograms);
			} else {
				return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
			}
		} catch (JSONException e) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
		}
	}
	@DELETE
	@Path("{deviceType}/programs/{programId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addStockProgram(@PathParam("deviceType") String deviceType, @PathParam("programId") int programId, @HeaderParam("Token") String authHeader) {
		if (!ApplicationUtils.isAuthenticated(authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		JSONObject stockPrograms = StorageManager.getInstance().getDeviceRegistryManager().removeStockProgram(deviceType, programId);
		if (stockPrograms != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, stockPrograms);
		} else {
			return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
		}
	}
	
}

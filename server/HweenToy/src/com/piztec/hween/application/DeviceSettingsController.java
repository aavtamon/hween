package com.piztec.hween.application;

import java.util.Iterator;

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
		
		JSONObject programs = new JSONObject();
		JSONObject stockPrograms = StorageManager.getInstance().getDeviceRegistryManager().getStockPrograms(deviceType);
		for (Iterator<String> it = stockPrograms.keys(); it.hasNext(); ) {
			final String programId = it.next();
			try {
				JSONObject stockProgram = stockPrograms.getJSONObject(programId);
				JSONObject program = new JSONObject(stockProgram.toString());
				program.remove("commands");
				programs.put(programId, program);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (stockPrograms != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, programs);
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
			
			int programId = StorageManager.getInstance().getDeviceRegistryManager().addStockProgram(deviceType, program);
			if (programId != -1) {
				JSONObject stockPrograms = StorageManager.getInstance().getDeviceRegistryManager().getStockPrograms(deviceType);
				return ControllerUtils.buildResponse(Response.Status.OK, stockPrograms, programId + "");
			} else {
				return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
			}
		} catch (JSONException e) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
		}
	}
	@OPTIONS
	@Path("{deviceType}/programs/{programId}")
	public Response getStockProgramOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	@GET
	@Path("{deviceType}/programs/{programId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStockProgram(@PathParam("deviceType") String deviceType, @PathParam("programId") int programId, @HeaderParam("Token") String authHeader) {
		if (!ApplicationUtils.isAuthenticated(authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		JSONObject stockProgram = StorageManager.getInstance().getDeviceRegistryManager().getStockProgram(deviceType, programId);
		if (stockProgram != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, stockProgram);
		} else {
			return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
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

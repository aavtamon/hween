package com.piztec.hween.device;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

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

import com.piztec.hween.ControllerUtils;
import com.piztec.hween.persistance.StorageManager;

@Path("device")
public class DeviceController {
	@OPTIONS
	@Path("{serialNumber}")
	public Response deviceInfoOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
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

	
	@OPTIONS
	@Path("{serialNumber}/schedule")
	public Response deviceScheduleOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}

	@GET
	@Path("{serialNumber}/schedule")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceSchedule(@PathParam("serialNumber") String serialNumber, @HeaderParam("Secret") String authHeader) {
		if (!DeviceUtils.isAuthenticated(serialNumber, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		try {
			JSONObject schedule = StorageManager.getInstance().getDevicesManager().getDeviceSchedule(serialNumber);
			if (schedule != null) {
				JSONObject detailedSchedule = new JSONObject(schedule.toString());
				JSONArray programs = detailedSchedule.getJSONArray("programs");
				for (int programIndex = 0; programIndex < programs.length(); programIndex++) {
					JSONObject program = programs.getJSONObject(programIndex);
					int programId = program.getInt("id");
					JSONObject referenceProgram = StorageManager.getInstance().getDevicesManager().getDeviceLibraryProgram(serialNumber, programId);
					if (referenceProgram == null) {
						JSONObject registryInfo = StorageManager.getInstance().getDeviceRegistryManager().getDeviceInfo(serialNumber);
						String deviceType = registryInfo.getString("type");
						referenceProgram = StorageManager.getInstance().getDeviceRegistryManager().getStockProgram(deviceType, programId);
					}
					
					if (referenceProgram != null) {
						for (Iterator<String> programIt = referenceProgram.keys(); programIt.hasNext(); ) {
							String propName = programIt.next();
							program.put(propName, referenceProgram.get(propName));
						}
					}
				}
		
				return ControllerUtils.buildResponse(Response.Status.OK, detailedSchedule);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
	}

	@GET
	@Path("{serialNumber}/code_version")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceCodeVersion(@PathParam("serialNumber") String serialNumber, @HeaderParam("Secret") String authHeader) {
		if (!DeviceUtils.isAuthenticated(serialNumber, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		String codeVersion = StorageManager.getInstance().getCodeUpgradeManager().getVersion(serialNumber);
		if (codeVersion != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, codeVersion);
		}
		
		return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
	}
	
	@GET
	@Path("{serialNumber}/code_image")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDeviceImage(@PathParam("serialNumber") String serialNumber, @HeaderParam("Secret") String authHeader) {
		if (!DeviceUtils.isAuthenticated(serialNumber, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		byte[] image = StorageManager.getInstance().getCodeUpgradeManager().getImage(serialNumber);
		if (image != null) {			
			return ControllerUtils.buildResponse(Response.Status.OK, new ByteArrayInputStream(image));
		}
		
		return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
	}
	
	@GET
	@Path("{serialNumber}/code_object")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceCode(@PathParam("serialNumber") String serialNumber, @HeaderParam("Secret") String authHeader) {
		if (!DeviceUtils.isAuthenticated(serialNumber, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		JSONObject codeObject = new JSONObject();
		try {
			codeObject.put("version", StorageManager.getInstance().getCodeUpgradeManager().getVersion(serialNumber));
			codeObject.put("schedule", "now");
			codeObject.put("image", StorageManager.getInstance().getCodeUpgradeManager().getImage(serialNumber));
			
			return ControllerUtils.buildResponse(Response.Status.OK, codeObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
	}
}

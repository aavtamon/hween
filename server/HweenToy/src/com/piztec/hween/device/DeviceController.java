package com.piztec.hween.device;

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
					JSONObject libraryProgram = StorageManager.getInstance().getDevicesManager().getDeviceLibraryProgram(serialNumber, programId);
					for (Iterator<String> programIt = libraryProgram.keys(); programIt.hasNext(); ) {
						String propName = programIt.next();
						program.put(propName, libraryProgram.get(propName));
					}
				}
		
				return ControllerUtils.buildResponse(Response.Status.OK, detailedSchedule);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
	}

}

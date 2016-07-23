package com.piztec.hween.application;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.http.OPTIONS;
import org.json.JSONObject;

import com.piztec.hween.ControllerUtils;
import com.piztec.hween.persistance.StorageManager;

@Path("user")
public class UserAccountController {
	@OPTIONS
	public Response loginOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("login") String login) {
		if (login == null) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
		}
		
		int userId = StorageManager.getInstance().getUserAccountManager().getUserId(login);
		if (userId != -1) {
			return ControllerUtils.buildResponse(Response.Status.OK, "{\"user_id\": " + userId + "}");
		} else {
			return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
		}
	}
	

	
	@OPTIONS
	@Path("{userId}/logout")
	public Response logoutOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}

	@GET
	@Path("{userId}/logout")
	@Produces(MediaType.TEXT_PLAIN)
	public Response logout(@PathParam("userId") int userId, @HeaderParam("Token") String authHeader) {
		if (!ApplicationUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		return ControllerUtils.buildResponse(Response.Status.OK, "Successfully logged out USER '" + userId + "'");
	}

	
	@OPTIONS
	@Path("{userId}")
	public Response getUserProfileOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}

	@GET
	@Path("{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserProfile(@PathParam("userId") int userId, @HeaderParam("Token") String authHeader) {
		if (!ApplicationUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}

		JSONObject userProfile = StorageManager.getInstance().getUserAccountManager().getUserProfile(userId);
		if (userProfile != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, userProfile);
		} else {
			return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
		}
	}
	

	@OPTIONS
	@Path("{userId}")
	public Response registerUserOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)	
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(String profileText) {
		try {
			JSONObject profile = new JSONObject(profileText);
			int userId = StorageManager.getInstance().getUserAccountManager().createUserAccount(profile);

			return ControllerUtils.buildResponse(Response.Status.CREATED, profile, userId + "");
		} catch (Exception e) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST, e.getMessage());
		}
	}

	
	
	@OPTIONS
	@Path("{userId}/settings")
	public Response getUserSettingsOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}

	@GET
	@Path("{userId}/settings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserSettings(@PathParam("userId") int userId, @HeaderParam("Token") String authHeader) {
		if (!ApplicationUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		JSONObject userSettings = StorageManager.getInstance().getUserAccountManager().getUserSettings(userId);
		if (userSettings != null) {
			return ControllerUtils.buildResponse(Response.Status.OK, userSettings);
		} else {
			return ControllerUtils.buildResponse(Response.Status.NOT_FOUND);
		}
	}
}

package com.piztec.hween.application;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.http.OPTIONS;
import org.json.JSONObject;

@Path("user")
public class UserAccountController {
	@OPTIONS
	public Response loginOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("login") String login, @HeaderParam("Token") String authHeader) {
		if (login == null) {
			return ControllerUtils.buildResponse(Response.Status.BAD_REQUEST);
		}
		
		return ControllerUtils.buildResponse(Response.Status.OK, "{\"login\": \"" + login + "\", \"user_id\": 1}");
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
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
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
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		String[] authTokens = authHeader.split(":");
		String profile = "{\"login\": \"" + authTokens[0] + "\", \"user_id\": 1, \"name\": \"Antosha\"}";
		
		return ControllerUtils.buildResponse(Response.Status.OK, profile);
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

			return ControllerUtils.buildResponse(Response.Status.CREATED, profile, "2");
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
		if (!ControllerUtils.isAuthenticated(userId, authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		String profile = "{}";
		
		return ControllerUtils.buildResponse(Response.Status.OK, profile);
	}
}

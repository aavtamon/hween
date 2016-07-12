package com.piztec.hween;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.wink.common.http.OPTIONS;
import org.json.JSONObject;

@Path("user")
public class ApplicationUserAccountController {
	@OPTIONS
	public Response loginOptions() {
		return buildResponse(Response.Status.OK);
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("login") String login, @HeaderParam("Token") String authHeader) {
		if (login == null) {
			return buildResponse(Response.Status.BAD_REQUEST);
		}
		
		return buildResponse(Response.Status.OK, "{\"login\": \"" + login + "\", \"user_id\": 1}");
	}
	

	
	@OPTIONS
	@Path("{userId}/logout")
	public Response logoutOptions() {
		return buildResponse(Response.Status.OK);
	}

	@GET
	@Path("{userId}/logout")
	@Produces(MediaType.TEXT_PLAIN)
	public Response logout(@PathParam("userId") int userId, @HeaderParam("Token") String authHeader) {
		if (!isAuthenticated(userId, authHeader)) {
			return buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		return buildResponse(Response.Status.OK, "Successfully logged out USER '" + userId + "'");
	}

	
	@OPTIONS
	@Path("{userId}")
	public Response getUserProfileOptions() {
		return buildResponse(Response.Status.OK);
	}

	@GET
	@Path("{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserProfile(@PathParam("userId") int userId, @HeaderParam("Token") String authHeader) {
		if (!isAuthenticated(userId, authHeader)) {
			return buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		String[] authTokens = authHeader.split(":");
		String profile = "{\"login\": \"" + authTokens[0] + "\", \"user_id\": 1, \"name\": \"Antosha\"}";
		
		return buildResponse(Response.Status.OK, profile);
	}
	

	@OPTIONS
	@Path("{userId}")
	public Response registerUserOptions() {
		return buildResponse(Response.Status.OK);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)	
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(String profileText) {
		try {
			JSONObject profile = new JSONObject(profileText);

			return buildResponse(Response.Status.CREATED, profile, "2");
		} catch (Exception e) {
			return buildResponse(Response.Status.BAD_REQUEST, e.getMessage());
		}
	}

	
	
	@OPTIONS
	@Path("{userId}/settings")
	public Response getUserSettingsOptions() {
		return buildResponse(Response.Status.OK);
	}

	@GET
	@Path("{userId}/settings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserSettings(@PathParam("userId") int userId, @HeaderParam("Token") String authHeader) {
		if (!isAuthenticated(userId, authHeader)) {
			return buildResponse(Response.Status.UNAUTHORIZED);
		}
		
		String[] authTokens = authHeader.split(":");
		String profile = "{}";
		
		return buildResponse(Response.Status.OK, profile);
	}
	

	
	
	
	private boolean isAuthenticated(final int userId, final String authHeader) {
		if (authHeader == null) {
			return false;
		}
		
		String[] authTokens = authHeader.split(":");
		if (authTokens.length != 2) {
			return false;
		}
		
		if (authTokens[0] == null || authTokens[1] == null) {
			return false;
		}
		
		return true;
	}
	
	
	private Response buildResponse(final Response.Status status) {
		return buildResponse(status, null, null);
	}
	
	private Response buildResponse(final Response.Status status, final Object body) {
		return buildResponse(status, body, null);
	}
	
	private Response buildResponse(final Response.Status status, final Object body, final String location) {
		ResponseBuilder builder = Response.status(status);
		
		if (body != null) {
			builder.entity(body);
		}
		if (location != null) {
			builder.header("Location", location);
		}
		
		builder.header("Access-Control-Allow-Origin", "*");
		builder.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, token, location");
		builder.header("Access-Control-Expose-Headers", "location");
		builder.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		
		return builder.build();
	}
}

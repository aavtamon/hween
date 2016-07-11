package com.piztec.hween;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.piztec.hween.user.UserProfile;

@Path("user")
public class ApplicationUserAccountController {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("login") String login, @HeaderParam("Token") String authHeader) {
		if (login == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		return Response.status(Response.Status.OK).entity("{'login': '" + login + "', 'user_id': 1}").build();
	}
	
	@GET
	@Path("{userId}/logout")
	@Produces(MediaType.TEXT_PLAIN)
	public Response logout(@PathParam("userId") int userId, @HeaderParam("Token") String authHeader) {
		if (!isAuthenticated(userId, authHeader)) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		return Response.status(Response.Status.OK).entity("Successfully logged out USER '" + userId + "'").build();
	}

	@GET
	@Path("{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserProfile(@PathParam("userId") int userId, @HeaderParam("Token") String authHeader) {
		if (!isAuthenticated(userId, authHeader)) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		String[] authTokens = authHeader.split(":");
		String profile = "{'login': '" + authTokens[0] + "', 'user_id': 1}";
		
		return Response.status(Response.Status.OK).entity(profile).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(UserProfile profile, @HeaderParam("Token") String authHeader) {
		return Response.status(Response.Status.OK).entity(profile).build();
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
}

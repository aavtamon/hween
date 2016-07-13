package com.piztec.hween.application;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

public class ControllerUtils {
	public static Response buildResponse(final Response.Status status) {
		return buildResponse(status, null, null);
	}
	
	public static Response buildResponse(final Response.Status status, final Object body) {
		return buildResponse(status, body, null);
	}
	
	public static Response buildResponse(final Response.Status status, final Object body, final String location) {
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
	
	
	public static boolean isAuthenticated(final String authHeader) {
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
	
	public static boolean isAuthenticated(final int userId, final String authHeader) {
		if (!isAuthenticated(authHeader)) {
			return false;
		}
		
		return true;
	}
}

package com.piztec.hween;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONArray;
import org.json.JSONException;

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
	
//	
//	
//	public static JSONArray removeFromArray(JSONArray arr, Object element) throws JSONException {
//		JSONArray result = new JSONArray();
//		
//		for (int i = 0; i < arr.length(); i++) {
//			if (!arr.get(i).equals(element)) {
//				result.put(arr.get(i));
//			}
//		}
//		
//		return result;
//	}
}

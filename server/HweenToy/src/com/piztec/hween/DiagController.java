package com.piztec.hween;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.http.OPTIONS;

@Path("")
public class DiagController {
	@OPTIONS
	public Response getOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response get() {
		return ControllerUtils.buildResponse(Response.Status.OK, "Service OK");
	}

}

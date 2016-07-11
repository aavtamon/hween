package com.piztec.hween;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("demo")
public class ToyController {
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		return "Jopa";
	}
}

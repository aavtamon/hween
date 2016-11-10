package com.piztec.hween.admin;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.http.OPTIONS;

import com.piztec.hween.ControllerUtils;
import com.piztec.hween.persistance.StorageManager;

@Path("admin")
public class AdminController {
	@OPTIONS
	@Path("code_images/reload")
	public Response deviceInfoOptions() {
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
	
	@PUT
	@Path("code_images/reload")
	@Produces(MediaType.APPLICATION_JSON)
	public Response reloadCodeImages(String body, @HeaderParam("Credentials") String authHeader) {
		if (!AdminUtils.isAuthorized(authHeader)) {
			return ControllerUtils.buildResponse(Response.Status.UNAUTHORIZED);
		}
		
	  StorageManager.getInstance().getCodeUpgradeManager().rehash();
		return ControllerUtils.buildResponse(Response.Status.OK);
	}
}

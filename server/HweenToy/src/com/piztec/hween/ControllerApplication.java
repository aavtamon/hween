package com.piztec.hween;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ControllerApplication extends Application {
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(ToyController.class);
		classes.add(ApplicationUserAccountController.class);
		return classes;
	}
}

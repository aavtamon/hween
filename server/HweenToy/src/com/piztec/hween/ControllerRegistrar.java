package com.piztec.hween;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.piztec.hween.application.DeviceSettingsController;
import com.piztec.hween.application.UserAccountController;
import com.piztec.hween.toy.ToyController;

public class ControllerRegistrar extends Application {
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(ToyController.class);
		classes.add(UserAccountController.class);
		classes.add(DeviceSettingsController.class);
		return classes;
	}
}

package com.piztec.hween;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.piztec.hween.application.DeviceSettingsController;
import com.piztec.hween.application.UserAccountController;
import com.piztec.hween.application.UserDeviceManagementController;
import com.piztec.hween.device.DeviceController;

public class ControllerRegistrar extends Application {
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(DeviceController.class);
		classes.add(UserAccountController.class);
		classes.add(DeviceSettingsController.class);
		classes.add(UserDeviceManagementController.class);
		return classes;
	}
}

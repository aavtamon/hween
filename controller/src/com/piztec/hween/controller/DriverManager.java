package com.piztec.hween.controller;

import com.piztec.hween.controller.drivers.DeviceDriver;
import com.piztec.hween.controller.drivers.StumpGhostDriver;

public class DriverManager {
	private static final String STUMP_GHOST_TYPE = "stump_ghost"; 
	
	private static final DriverManager instance = new DriverManager();
	
	private String deviceType;
	private DeviceDriver driver;
	
	private DriverManager() {
		readDeviceConfig();
		
		if (deviceType == STUMP_GHOST_TYPE) {
			driver = new StumpGhostDriver();
		}
		
	}
	
	public static DeviceDriver getDriver() {
		return instance.driver;
	}
	
	private void readDeviceConfig() {
		//TODO
		deviceType = STUMP_GHOST_TYPE;
	}
}

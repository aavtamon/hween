package com.piztec.hween.controller;

import org.json.JSONObject;

import com.piztec.hween.controller.drivers.DeviceDriver;
import com.piztec.hween.controller.drivers.StumpGhostDriver;


public class DeviceManager {
	private static final String STUMP_GHOST_TYPE = "stump_ghost";
	
	private static final String DEVICE_MODE_IDLE = "idle";
	private static final String DEVICE_MODE_RUNNING = "running";
	private static final String DEVICE_MODE_MANUAL = "manual";
	
	private static final DeviceManager instance = new DeviceManager();
	
	private String deviceType;
	private DeviceDriver driver;
	private Schedule schedule;
	private String mode;
	
	private DeviceManager() {
		readDeviceConfig();
		
		if (deviceType == STUMP_GHOST_TYPE) {
			driver = new StumpGhostDriver();
		}
		
	}

	public static DeviceManager getInstance() {
		return instance;
	}
	
	
	public void setMode(final String mode) {
		if (this.mode == mode) {
			return;
		}
		
		this.mode = mode;
		
		if (schedule != null) {
			if (mode == DEVICE_MODE_RUNNING) {
				schedule.execute();
			} else if (mode == DEVICE_MODE_MANUAL || mode == DEVICE_MODE_IDLE) {
				schedule.interrupt();
			}
		}
	}
	
	
	public void setSchedule(final JSONObject cloudSchedule) {
		if (this.schedule != null) {
			this.schedule.interrupt();
		}
		
		this.schedule = new Schedule(cloudSchedule, driver);
		
		if (mode == DEVICE_MODE_RUNNING) {
			schedule.execute();
		} else if (mode == DEVICE_MODE_MANUAL || mode == DEVICE_MODE_IDLE) {
			schedule.interrupt();
		}
	}
	
	
	
	private void readDeviceConfig() {
		//TODO
		deviceType = STUMP_GHOST_TYPE;
	}
}

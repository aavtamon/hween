package com.piztec.hween.controller;

import org.json.JSONObject;

import com.piztec.hween.controller.drivers.DeviceDriver;
import com.piztec.hween.controller.drivers.DeviceDriver.Command;
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
		
		if (STUMP_GHOST_TYPE.equals(deviceType)) {
			driver = new StumpGhostDriver();
		}
		
	}

	public static DeviceManager getInstance() {
		return instance;
	}
	
	
	public void setMode(final String mode) {
		System.out.println("DeviceManager: new mode was set: " + mode);
		
		if (mode.equals(this.mode)) {
			return;
		}
		
		this.mode = mode;

		if (schedule != null) {
			if (DEVICE_MODE_RUNNING.equals(mode)) {
				schedule.execute();
			} else if (DEVICE_MODE_MANUAL.equals(mode) || DEVICE_MODE_IDLE.equals(mode)) {
				schedule.interrupt();
			}
		}
	}
	
	
	public void setSchedule(final JSONObject cloudSchedule) {
		System.out.println("DeviceManager: new schedule was set: " + cloudSchedule);
		
		if (this.schedule != null) {
			this.schedule.interrupt();
		}
		
		this.schedule = new Schedule(cloudSchedule, driver);
		
		if (DEVICE_MODE_RUNNING.equals(mode)) {
			schedule.execute();
		} else if (DEVICE_MODE_MANUAL.equals(mode) || DEVICE_MODE_IDLE.equals(mode)) {
			schedule.interrupt();
		}
	}
	
	public void executeCommand(final String commandName, final String arg) {
		setMode(DEVICE_MODE_MANUAL);
		
		Command command = driver.getCommand(commandName);
		if (command != null) {
			try {
				command.execute(arg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	private void readDeviceConfig() {
		//TODO
		deviceType = STUMP_GHOST_TYPE;
	}
}

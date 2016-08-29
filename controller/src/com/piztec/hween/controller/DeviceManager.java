package com.piztec.hween.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.json.JSONObject;

import com.piztec.hween.controller.drivers.DeviceDriver;
import com.piztec.hween.controller.drivers.DeviceDriver.Command;
import com.piztec.hween.controller.drivers.StumpGhostDriver;
import com.piztec.hween.controller.network.ConnectionManager;


public class DeviceManager {
	private static final String DEFAULT_CONFIG_FILE = "controller.properties";

	
	private static final String STUMP_GHOST_TYPE = "stump_ghost";
	
	private static final String DEVICE_MODE_IDLE = "idle";
	private static final String DEVICE_MODE_RUNNING = "running";
	private static final String DEVICE_MODE_MANUAL = "manual";
	
	private static DeviceManager instance;
	
	private boolean deviceFeaturesDisabled;
	
	private DeviceDriver driver;
	private Schedule schedule;
	private String mode;
	
	private DeviceDescriptor deviceDescriptor;
	
	
	static class DeviceDescriptor {
		String serialNumber;
		String secret;
		String deviceType;
	}
	
	
	
	private DeviceManager() {
		try {
			readDeviceConfig();
		} catch (Exception e) {
			System.err.println("Error reading device config file");
			return;
		}
		
		if (STUMP_GHOST_TYPE.equals(deviceDescriptor.deviceType)) {
			driver = new StumpGhostDriver(!deviceFeaturesDisabled);
		}		
	}

	public static DeviceManager getInstance() {
		if (instance == null) {
			 instance = new DeviceManager();
		}
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
	
	
	public String getSerialNumber() {
		return deviceDescriptor.serialNumber;
	}
	
	public String getDeviceSecret() {
		return deviceDescriptor.secret;
	}
	
	
	
	
	private void readDeviceConfig() throws Exception {
		String configFilePath = System.getProperty("config_file", DEFAULT_CONFIG_FILE);
		System.out.println("Config file path = " + configFilePath);
		
		
		InputStream propFileStream = new FileInputStream(configFilePath);
		Properties props = new Properties();
		try {
			props.load(propFileStream);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		if (!props.getProperty("pc", "no").equals("no")) {
			System.out.println("WARNING: Target device features are disabled");
			deviceFeaturesDisabled = true;
		}
		ConnectionManager.setPreferredInterface(props.getProperty("primary_network_interface"));

		deviceDescriptor = new DeviceDescriptor();
		deviceDescriptor.serialNumber = props.getProperty("serial_number");
		deviceDescriptor.secret = props.getProperty("secret");		
		deviceDescriptor.deviceType = props.getProperty("deviceType");	
	}
}

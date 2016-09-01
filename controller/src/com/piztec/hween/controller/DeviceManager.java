package com.piztec.hween.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.json.JSONObject;

import com.piztec.hween.controller.drivers.DeviceDriver;
import com.piztec.hween.controller.drivers.DeviceDriver.Button;
import com.piztec.hween.controller.drivers.DeviceDriver.Command;
import com.piztec.hween.controller.drivers.DeviceDriver.Indicator;
import com.piztec.hween.controller.drivers.StumpGhostDriver;
import com.piztec.hween.controller.media.NetworkManager;
import com.piztec.hween.controller.media.NetworkManager.AddressDescriptor;


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
		
		
		Button wpsButton = driver.getButton(DeviceDriver.BUTTON_WPS);
		if (wpsButton != null) {
			wpsButton.addListener(new Button.ButtonListener() {
				public void onPressed() {
					final Indicator wpsIndicator = driver.getIndicator(DeviceDriver.INDICATOR_WPS);
					if (wpsIndicator != null) {
						wpsIndicator.blink(1000);
					}
					
					NetworkManager.getInstance().wpsConnect(new NetworkManager.ConnectionListener() {
						public void onConnectionStatusChanged(final String status) {
							if (status == NetworkManager.ConnectionListener.STATUS_FAILED) {
								wpsIndicator.turnOff();
							} else if (status == NetworkManager.ConnectionListener.STATUS_COMPLETED) {
								wpsIndicator.turnOn();
							}
						}
					});
				}
			});
		}
		
		startNetworkMonitoringThread();
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
		System.out.println("DeviceManager: new schedule was set");
		
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
	
	
	
	
	private void startNetworkMonitoringThread() {
		new Thread() {
			public void run() {
				Indicator networkIndicator = driver.getIndicator(DeviceDriver.INDICATOR_NETWORK);
				if (networkIndicator == null) {
					return;
				}
				
				while (true) {
					AddressDescriptor address = NetworkManager.getInstance().getIPAddress();
					if (address != null) {
						if (NetworkManager.getInstance().ensureConnectivity()) {
							networkIndicator.turnOn();
						} else {
							networkIndicator.blink(1000);
						}
					} else {
						networkIndicator.turnOff();
					}
					
					try {
						Thread.sleep(5000);
					} catch (Exception e) {
					}
				}
			}
		}.start();
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
		NetworkManager.setPreferredInterface(props.getProperty("primary_network_interface"));

		deviceDescriptor = new DeviceDescriptor();
		deviceDescriptor.serialNumber = props.getProperty("serial_number");
		deviceDescriptor.secret = props.getProperty("secret");		
		deviceDescriptor.deviceType = props.getProperty("deviceType");	
	}
}

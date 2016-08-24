package com.piztec.hween.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
	private static final String DEFAULT_CONFIG_FILE = "controller.properties";
	
	public static void main(String[] args) throws Exception {
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
		
		String url = props.getProperty("server_url");
		
		CloudAccessor.DeviceDescriptor dd = new CloudAccessor.DeviceDescriptor();
		dd.serialNumber = props.getProperty("serial_number");
		dd.bssid = props.getProperty("bssid");
		dd.secret = props.getProperty("secret");

		CloudAccessor ca = new CloudAccessor(url, dd);
		ca.start();
		
		ControlServer cs = new ControlServer(ca);
		cs.start();
	}
}

package com.piztec.hween.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
	private static final String CONFIG_FILE = "controller.properties";
	
	public static void main(String[] args) throws Exception {
		InputStream propFileStream = new FileInputStream(CONFIG_FILE);//Main.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
		Properties props = new Properties();
		try {
			props.load(propFileStream);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		String url = props.getProperty("server_url");

		CloudAccessor.getInstance().start(url);
		ControlServer.getInstance().start();
	}
}

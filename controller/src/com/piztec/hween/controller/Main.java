package com.piztec.hween.controller;


public class Main {
	public static void main(String[] args) throws Exception {
		//Initialize the device
		DeviceManager.getInstance();
		
		String url = System.getProperty("server_url");
		if (url == null) {
			System.err.println("URL is not specified");
			return;
		}
		System.err.println("Server URL: " + url);
		
		ControllerContext.setServerUrl(url);
		
		CloudAccessor ca = new CloudAccessor();
		ca.start();
		
		ControlServer cs = new ControlServer(ca);
		cs.start();
	}
}

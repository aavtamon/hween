package com.piztec.hween.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CloudAccessor {
	private static final String SERVER_URL = "http://localhost:8080/HweenToy/";
	
	private static CloudAccessor instance = new CloudAccessor();
	
	private Thread reportingThread;
	
	private CloudAccessor() {
	}
	
	public static CloudAccessor getInstance() {
		return instance;
	}

	
	public void startStatusReporting() {
		reportingThread = new Thread() {
			public void run() {
				while (!isInterrupted()) {
					reportStatus();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		
		reportingThread.start();
	}
	
	public synchronized void stopStatusReporting() {
		reportingThread.interrupt();
	}
	
	
	private void reportStatus() {
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL(SERVER_URL + "device/" + getSerialNumber()).openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Secret", getDeviceSecret());
			
			OutputStream output = connection.getOutputStream();
			output.write(("{\"ip_address\": \"" + getIPAddress() + "\", \"bssid\": \"" + getBssid() + "\"}").getBytes());
			output.close();
			
			InputStream response = connection.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(response));
	        StringBuilder responseText = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            responseText.append(line);
	        }
	        reader.close();
	        
	        System.out.println(responseText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private String getSerialNumber() {
		return "0000000002";
	}
	
	private String getIPAddress() {
		return "192.168.5.10";
	}

	private String getBssid() {
		return "35a785cd456";
	}
	
	private String getDeviceSecret() {
		return "secret-2";
	}
}

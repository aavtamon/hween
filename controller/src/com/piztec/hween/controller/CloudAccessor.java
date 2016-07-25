package com.piztec.hween.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CloudAccessor {
	private static final String SERVER_URL = "http://localhost:8080/HweenToy/";
	
	private static final int FAST_REPORTING_COUNT_LIMIT = 10;
	private static final int FAST_REPORTING_INTERVAL = 10 * 1000;
	private static final int NORMAL_REPORTING_INTERVAL = 60 * 1000;
	
	private static CloudAccessor instance;
	private final Thread reportingThread;
	private int reportingCount;
	private int reportingInterval = NORMAL_REPORTING_INTERVAL;
	
	private CloudAccessor() {
		reportingThread = new Thread() {
			public void run() {
				while (!isInterrupted()) {
					synchronized (CloudAccessor.this) {
						if (reportingInterval == FAST_REPORTING_INTERVAL) {
						    if (reportingCount < FAST_REPORTING_COUNT_LIMIT) {
						    	reportingCount++;
						    } else {
						    	reportingInterval = NORMAL_REPORTING_INTERVAL;
						    }
						}
					}
					
					reportStatus();
					try {
						Thread.sleep(reportingInterval);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		
		reportingThread.start();
	}
	
	public static CloudAccessor getInstance() {
		if (instance == null) {
			instance = new CloudAccessor();
		};
		
		return instance;
	}
	
	public void stop() {
		reportingThread.interrupt();
	}

	
	public synchronized void startOftenReporting() {
		reportingCount = 0;
		reportingInterval = FAST_REPORTING_INTERVAL;
	}
	
	public synchronized void stopOftenReporting() {
		reportingInterval = NORMAL_REPORTING_INTERVAL;
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
		return "0000000001";
	}
	
	private String getIPAddress() {
		return "192.168.5.10";
	}

	private String getBssid() {
		return "35a785cd456";
	}
	
	private String getDeviceSecret() {
		return "secret-1";
	}
}

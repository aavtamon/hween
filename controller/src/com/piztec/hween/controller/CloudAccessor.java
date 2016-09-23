package com.piztec.hween.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.piztec.hween.controller.media.NetworkManager;

public class CloudAccessor {
	private static final int FAST_REPORTING_COUNT_LIMIT = 10;
	private static final int FAST_REPORTING_INTERVAL = 10 * 1000;
	private static final int NORMAL_REPORTING_INTERVAL = 60 * 1000;
	
	private final Thread reportingThread;
	private int reportingCount;
	private int reportingInterval = NORMAL_REPORTING_INTERVAL;
	
	private long lastReportedScheduleRevision = -1;
	private String lastReportedMode = null;
	
	public CloudAccessor() {
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
					
					synchronized (CloudAccessor.this) {
						try {
							CloudAccessor.this.wait(reportingInterval);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
	}
	
	public synchronized void start() {
		reportingInterval = NORMAL_REPORTING_INTERVAL;
		
		reportingThread.start();
	}

	public void stop() {
		reportingThread.interrupt();
	}

	
	public synchronized void enableOftenReporting() {
		reportingCount = 0;
		reportingInterval = FAST_REPORTING_INTERVAL;
		notify();
	}
		
	
	private void reportStatus() {
		NetworkManager.AddressDescriptor address = NetworkManager.getInstance().getIPAddress();
		if (address == null) {
			System.err.println("Cannot determine self ip address. Report to the server cannot be sent");
			return;
		}
		
		String bssid = null;
		if (address.type == NetworkManager.AddressDescriptor.TYPE_WIFI) {
			bssid = NetworkManager.getInstance().getConnectedAccessPoint().bssid;
		} 
		
		String statusToReport = "{\"ip_address\": \"" + address.ipAddress + "\", \"port\": " + ControlServer.SERVER_PORT + (bssid != null ? ", \"bssid\": \"" + bssid + "\"" : "") + "}";
		System.out.println("Reporting back to " + ControllerContext.getServerUrl() + ": " + statusToReport);

		String response = connectToCloudServer("PUT", "", statusToReport);
		if (response != null) {
			handleCloudResponse(response);
		}
	}
	
	private void handleCloudResponse(final String responseText) {
		try {
			JSONObject resposeObject = new JSONObject(responseText);
	        long scheduleRevision = resposeObject.getLong("schedule_revision");
	        
	        if (scheduleRevision != lastReportedScheduleRevision) {
	        	lastReportedScheduleRevision = scheduleRevision;
	        	
	        	String scheduleText = connectToCloudServer("GET", "schedule", null);
	        	if (scheduleText != null) {
	            	DeviceManager.getInstance().setSchedule(new JSONObject(scheduleText));        	
	        	}
	        }
	        
	        String codeVersion = resposeObject.getString("code_version");
	        
	        if (codeVersion != null && !codeVersion.equals(CodeUpgrader.getInstance().getCurrentImageVersion())) {
	        	String codeText = connectToCloudServer("GET", "code", null);
	        	if (codeText != null) {
	        		JSONObject codeObject = new JSONObject(codeText);
	        		
					CodeUpgrader.Upgrade upgrade = new CodeUpgrader.Upgrade();
					upgrade.schedule = codeObject.getString("schedule");
					upgrade.version = codeObject.getString("version");
					upgrade.image = codeObject.getString("image");
					
					CodeUpgrader.getInstance().upgrade(upgrade);
	        	}
	        }

	        String mode = resposeObject.getString("mode");
	        if (!mode.equals(lastReportedMode)) {
	        	lastReportedMode = mode;
	        	
	        	DeviceManager.getInstance().setMode(mode);
	        }
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	private String connectToCloudServer(final String httpMethod, final String resource, final String body) {
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL(ControllerContext.getServerUrl() + "/device/" + DeviceManager.getInstance().getSerialNumber() + "/" + resource).openConnection();
			if ("PUT".equalsIgnoreCase(httpMethod)) {
				connection.setDoOutput(true);
			}
			connection.setRequestMethod(httpMethod);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Secret", DeviceManager.getInstance().getDeviceSecret());
			
			if (body != null) {
				OutputStream output = connection.getOutputStream();
				output.write(body.getBytes());
				output.close();
			}
			
			InputStream response = connection.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(response));
	        StringBuilder responseText = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            responseText.append(line);
	        }
	        reader.close();
	        
	        return responseText.toString();
		} catch (Exception e) {
			System.err.println("Problem accessing " + ControllerContext.getServerUrl());
			e.printStackTrace();
			
			return null;
		}
	}
}

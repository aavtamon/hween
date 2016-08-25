package com.piztec.hween.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

public class CloudAccessor {
	private static final int FAST_REPORTING_COUNT_LIMIT = 10;
	private static final int FAST_REPORTING_INTERVAL = 10 * 1000;
	private static final int NORMAL_REPORTING_INTERVAL = 60 * 1000;
	
	private final Thread reportingThread;
	private int reportingCount;
	private int reportingInterval = NORMAL_REPORTING_INTERVAL;
	
	private String lastReportedSchedule = null;
	private String lastReportedMode = null;
	private String serverUrl = null;
	private DeviceDescriptor deviceDescriptor;
	
	static class DeviceDescriptor {
		String serialNumber;
		String bssid;
		String secret;
		String primaryNetworkInterface;
	}
	
	public CloudAccessor(final String serverUrl, final DeviceDescriptor deviceDescriptor) {
		this.deviceDescriptor = deviceDescriptor;
		this.serverUrl = serverUrl;
		
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
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL(serverUrl + "/device/" + getSerialNumber()).openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Secret", getDeviceSecret());
			
			String statusToReport = "{\"ip_address\": \"" + getIPAddress() + "\", \"port\": " + getPort() + ", \"bssid\": \"" + getBssid() + "\"}";
			System.out.println("Reporting back to " + serverUrl + ": " + statusToReport);
			OutputStream output = connection.getOutputStream();
			output.write(statusToReport.getBytes());
			output.close();
			
			InputStream response = connection.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(response));
	        StringBuilder responseText = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            responseText.append(line);
	        }
	        reader.close();
	        
	        handleCloudResponse(responseText.toString());
		} catch (Exception e) {
			System.err.println("Problem accessing " + serverUrl);
			e.printStackTrace();
		}
	}
	
	
	private void handleCloudResponse(final String responseText) throws JSONException {
		JSONObject resposeObject = new JSONObject(responseText);
        JSONObject schedule = resposeObject.getJSONObject("schedule");
        
        if (!schedule.toString().equals(lastReportedSchedule)) {
        	lastReportedSchedule = schedule.toString();
        	
        	DeviceManager.getInstance().setSchedule(schedule);        	
        }
        
        String mode = resposeObject.getString("mode");
        if (!mode.equals(lastReportedMode)) {
        	lastReportedMode = mode;
        	
        	DeviceManager.getInstance().setMode(mode);
        }
	}
	

	private String getSerialNumber() {
		return deviceDescriptor.serialNumber;
	}
	
	private String getIPAddress() {
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			while (nets.hasMoreElements()) {
				NetworkInterface net = nets.nextElement();
				if (deviceDescriptor.primaryNetworkInterface.equals(net.getName())) {
					Enumeration<InetAddress> addresses = net.getInetAddresses();
					if (addresses.hasMoreElements()) {
						return addresses.nextElement().getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "127.0.0.1";
	}

	private int getPort() {
		return ControlServer.SERVER_PORT;
	}

	private String getBssid() {
		return deviceDescriptor.bssid;
	}
	
	private String getDeviceSecret() {
		return deviceDescriptor.secret;
	}
}

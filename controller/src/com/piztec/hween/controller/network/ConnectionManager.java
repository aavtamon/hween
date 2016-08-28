package com.piztec.hween.controller.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class ConnectionManager {
	//https://wiki.debian.org/WiFi/HowToUse
	
	public static class AddressDescriptor {
		public static String TYPE_ETHERNET = "eth";
		public static String TYPE_WIFI = "wifi";
		
		public String ipAddress;
		public String type;
	}
	
	public static class AccessPointDescriptor {
		public String name;
		public String bssid;
	}
	
	public interface ConnectionListener {
		public static String STATUS_IN_PROGRESS = "in_progress";
		public static String STATUS_ASSOCIATED = "accosiated";
		public static String STATUS_COMPLETED = "completed";
		public static String STATUS_FAILED = "failed";
		
		void onConnectionStatusChanged(final String status);
	}
	
	
	private static ConnectionManager instance;
	private static String preferredInterfaceName;
	
	public static void setPreferredInterface(final String name) {
		preferredInterfaceName = name;
	}
	
	public static ConnectionManager getInstance() {
		if (instance == null) {
			instance = new ConnectionManager();
		}
		
		return instance;
	}
	
	public AddressDescriptor getIPAddress() {
		AddressDescriptor result = new AddressDescriptor();

		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			while (nets.hasMoreElements()) {
				NetworkInterface net = nets.nextElement();
				Enumeration<InetAddress> addresses = net.getInetAddresses();
				if (addresses.hasMoreElements()) {
					result.ipAddress = addresses.nextElement().getHostAddress();
					result.type = getInterfaceType(net.getName());
					
					if (preferredInterfaceName == null || preferredInterfaceName.equals(net.getName())) {
						return result;
					}
				}
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
	}

	
	// Wifi operations
	
	public AccessPointDescriptor getConnectedAccessPoint() {
		//wpa_cli status
		AccessPointDescriptor ap = new AccessPointDescriptor();
		
		return ap;
	}
	
	public AccessPointDescriptor[] scan() {
		//wpa_cli scan / wpa_cli scan_results 
		AccessPointDescriptor[] detectedNetworks = new AccessPointDescriptor[] {};
		
		return detectedNetworks;
	}
	
	
	public void wpsConnect(final ConnectionListener listener) {
		//wpa_cli wps_pbc
		String output = executeSystemCommand("wpa_cli wps_pbc");
		System.out.println("WPS Connect: " + output);
	}
	
	public void disconnect(final ConnectionListener listener) {
		//wpa_cli disconnect
		String output = executeSystemCommand("wpa_cli disconnect");
		System.out.println("Disconnect: " + output);
	}
	
	
	
	private static String getInterfaceType(final String interfaceName) {
		if (interfaceName.startsWith("eth")) {
			return AddressDescriptor.TYPE_ETHERNET;
		} else if (interfaceName.startsWith("wlan")) {
			return AddressDescriptor.TYPE_WIFI;
		} else {
			System.err.println("Unrecognized netwrok interface type: " + interfaceName);
		}
		
		return null;
	}
	
	private static String executeSystemCommand(final String cmd) {
		try {
			Process cmdProcess = Runtime.getRuntime().exec(cmd);
			InputStream cmdOutput = cmdProcess.getInputStream();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(cmdOutput));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
	        reader.close();
	        
	        return out.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}

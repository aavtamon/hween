package com.piztec.hween.controller.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		List<String> scanOutput = executeWpaCommand("scan");
		if (scanOutput == null || scanOutput.size() != 1) {
			System.err.println("Unexpected Scan comamnd output");
			return null;
		}
		
		if (!scanOutput.get(0).equals("OK")) {
			return null;
		}
		

		List<AccessPointDescriptor> detectedNetworks = new ArrayList<AccessPointDescriptor>();
		
		List<String> scanResultOutput = executeWpaCommand("scan_results");
		for (String line: scanResultOutput) {
			String[] network = line.split("\t");
			
			AccessPointDescriptor descriptor = new AccessPointDescriptor();
			descriptor.name = network.length >= 5 ? network[4] : "";
			descriptor.bssid = network[0];
			
			detectedNetworks.add(descriptor);
		}
		
		return detectedNetworks.toArray(new AccessPointDescriptor[detectedNetworks.size()]);
	}
	
	
	public boolean wpsConnect(final ConnectionListener listener) {
		//wpa_cli wps_pbc
		List<String> wpsOutput = executeWpaCommand("wps_pbc");
		if (wpsOutput == null || wpsOutput.size() != 1) {
			System.err.println("Unexpected WPS comamnd output");
			listener.onConnectionStatusChanged(ConnectionListener.STATUS_FAILED);
			return false;
		}
		
		if (!wpsOutput.get(0).equals("OK")) {
			listener.onConnectionStatusChanged(ConnectionListener.STATUS_FAILED);
			return false;
		}
		
		Map<String, String> statusOutput = getWpaStatus();
		String oldState = null;
		
		while (true) {
			String state = (String)statusOutput.get("wpa_state");
			System.out.println("WPS Connect: current state = " + state);
			
			if (oldState != state) {
				oldState = state;

				if (state == "SCANNING") {
					listener.onConnectionStatusChanged(ConnectionListener.STATUS_IN_PROGRESS);
				} else if (state == "COMPLETED") {
					listener.onConnectionStatusChanged(ConnectionListener.STATUS_COMPLETED);
					return true;
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}				
	}
	
	public void disconnect(final ConnectionListener listener) {
		//wpa_cli disconnect
		List<String> disconnectOutput = executeWpaCommand("disconnect");
		if (disconnectOutput == null || disconnectOutput.size() != 1) {
			System.err.println("Unexpected Disconnect comamnd output");
			listener.onConnectionStatusChanged(ConnectionListener.STATUS_FAILED);
		} else {
			if (disconnectOutput.get(0).equals("OK")) {
				listener.onConnectionStatusChanged(ConnectionListener.STATUS_IN_PROGRESS);
				
				Map<String, String> statusOutput = getWpaStatus();
				String oldState = null;
				
				while (true) {
					String state = (String)statusOutput.get("wpa_state");
					
					if (oldState != state) {
						oldState = state;
	
						if (state == "DISCONNECTED") {
							listener.onConnectionStatusChanged(ConnectionListener.STATUS_COMPLETED);
							break;
						}
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}				
			} else {
				listener.onConnectionStatusChanged(ConnectionListener.STATUS_FAILED);
			}
		}
	}
	
	
	
	
	private static Map<String, String> getWpaStatus() {
		List<String> statusOutput = executeWpaCommand("status");
		if (statusOutput == null) {
			return null;
		}
		
		Map<String, String> result = new HashMap<String, String>();
		for (String line: statusOutput) {
			String[] pair = line.split("=");
			if (pair.length != 2) {
				System.err.println("Parsing status output. Line <" + line + "> has an unexpected format");
			} else {
				result.put(pair[0], pair[1]);
			}
		}
		
		return result;
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
	
	private static List<String> executeWpaCommand(final String cmd) {
		try {
			Process cmdProcess = Runtime.getRuntime().exec("wpa_cli " + cmd);
			InputStream cmdOutput = cmdProcess.getInputStream();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(cmdOutput));
	        
			List<String> result = new ArrayList<String>();
	        String line = reader.readLine();
	        if (!line.startsWith("Selected interface")) {
	        	System.err.println("Unexpected wpa_cli output format. First line is " + line);	        	
	        }
	        
	        while ((line = reader.readLine()) != null) {
	            result.add(line);
	        }
	        reader.close();
	        
	        return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}

package com.piztec.hween.controller.network;

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

	public AccessPointDescriptor getAccessPoint() {
		AccessPointDescriptor ap = new AccessPointDescriptor();
		
		return ap;
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
}

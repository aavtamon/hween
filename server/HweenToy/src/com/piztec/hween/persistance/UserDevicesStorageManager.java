package com.piztec.hween.persistance;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * {
 * }
 */

public class UserDevicesStorageManager {
	private final JSONObject userDevicesStorage;
	
	UserDevicesStorageManager(JSONObject userDevicesRoot) {
		userDevicesStorage = userDevicesRoot;
	}
	
	
	public JSONObject getDeviceIds() {
		try {
			JSONObject result = new JSONObject();
			JSONArray registeredIds = new JSONArray();
			result.put("registered", registeredIds);
			JSONArray unregisteredIds = new JSONArray();
			result.put("unregistered", unregisteredIds);
			
			for (Iterator it = userDevicesStorage.keys(); it.hasNext(); ) {
				String key = it.next().toString();
				JSONObject deviceInfo = userDevicesStorage.getJSONObject(key);
				
				boolean registered = deviceInfo.getBoolean("registered");
				if (registered) {
					registeredIds.put(key);
				} else {
					unregisteredIds.put(key);
				}
			}
			return result;
		} catch (Exception e) {
			return null;
		}		
	}
	

	public boolean registerDevice(final int deviceId) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(deviceId + "");
			if (deviceInfo == null) {
				return false;
			}
			
			boolean registered = deviceInfo.getBoolean("registered");
			if (registered) {
				return false;
			}
			
			deviceInfo.put("registered", true);
			
			StorageManager.getInstance().commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean unregisterDevice(final int deviceId) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(deviceId + "");
			if (deviceInfo == null) {
				return false;
			}
			
			boolean registered = deviceInfo.getBoolean("registered");
			if (!registered) {
				return false;
			}
			
			deviceInfo.put("registered", false);
			
			StorageManager.getInstance().commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public int addDevice(final String serialNumber) {
		try {
			JSONObject registryInfo = StorageManager.getInstance().getDeviceRegistryManager().getDeviceInfo(serialNumber);
			
			int deviceId = Integer.parseInt(serialNumber);

			JSONObject deviceInfo = new JSONObject();
			
			String deviceType = registryInfo.getString("type");
			
			deviceInfo.put("id", deviceId);
			deviceInfo.put("name", registryInfo.getString("name"));
			deviceInfo.put("type", deviceType);
			deviceInfo.put("version", registryInfo.getString("version"));
			deviceInfo.put("serial_number", registryInfo.getString("serial_number"));
			deviceInfo.put("status", "connected");
			deviceInfo.put("ip_address", "192.168.0.100");
			deviceInfo.put("schedule", StorageManager.getInstance().getDeviceRegistryManager().getDeviceSchedule(deviceType));			
			deviceInfo.put("mode", StorageManager.getInstance().getDeviceRegistryManager().getDeviceMode(deviceType));
			deviceInfo.put("registered", true);
			
			userDevicesStorage.put(deviceId + "", deviceInfo);
			
			StorageManager.getInstance().commit();

			return deviceId;
		} catch (Exception e) {
			return -1;
		}
	}
	
	
	public boolean removeDevice(final int deviceId) {
		Object removedDevice = userDevicesStorage.remove(deviceId + "");
		
		StorageManager.getInstance().commit();
		
		return removedDevice != null;
	}
	
	
	public JSONObject getDeviceInfo(final int deviceId) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(deviceId + "");
			if (deviceInfo == null) {
				return null;
			}
			
			JSONObject result = new JSONObject();
			
			result.put("id", deviceId);
			result.put("name", deviceInfo.getString("name"));
			result.put("type", deviceInfo.getString("type"));
			result.put("version", deviceInfo.getString("version"));
			result.put("serial_number", deviceInfo.getString("serial_number"));
			result.put("status", deviceInfo.getString("status"));
			result.put("ip_address", deviceInfo.getString("ip_address"));

			return result;
		} catch (Exception e) {
			return null;
		}		
	}
	
	public JSONObject getDeviceSchedule(final int deviceId) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(deviceId + "");
			if (deviceInfo == null) {
				return null;
			}
			
			return deviceInfo.getJSONObject("schedule");
		} catch (Exception e) {
			return null;
		}		
	}
	
	public JSONObject setDeviceSchedule(final int deviceId, final JSONObject schedule) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(deviceId + "");
			if (deviceInfo == null) {
				return null;
			}
			
			deviceInfo.put("schedule", schedule);
			
			StorageManager.getInstance().commit();
			
			return schedule;
		} catch (Exception e) {
			return null;
		}		
	}	
	
	public String getDeviceMode(final int deviceId) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(deviceId + "");
			if (deviceInfo == null) {
				return null;
			}
			
			return deviceInfo.getString("mode");
		} catch (Exception e) {
			return null;
		}		
	}
	
	public String setDeviceMode(final int deviceId, final String mode) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(deviceId + "");
			if (deviceInfo == null) {
				return null;
			}
			
			deviceInfo.put("mode", mode);
			
			StorageManager.getInstance().commit();
			
			return mode;
		} catch (Exception e) {
			return null;
		}		
	}	
}

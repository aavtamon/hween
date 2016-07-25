package com.piztec.hween.persistance;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * {
 * }
 */

public class DevicesStorageManager {
	private static final long STATUS_VALIDITY_TIMEOUT = 60 * 1000; //1 min
	
	private final JSONObject userDevicesStorage;
	
	
	DevicesStorageManager(JSONObject userDevicesRoot) {
		userDevicesStorage = userDevicesRoot;
	}
	
	public void reportDevice(final String serialNumber, final JSONObject reportedInfo) {
		try {
			JSONObject info = null;
			try {
				info = userDevicesStorage.getJSONObject(serialNumber);
			} catch (Exception e) {
			}
			if (info == null) {
				info = new JSONObject();
			}

			info.put("ip_address", reportedInfo.get("ip_address"));
			info.put("bssid", reportedInfo.get("bssid"));
			info.put("status", "connected");
			info.put("status_update_timestamp", System.currentTimeMillis());
			
			userDevicesStorage.put(serialNumber, info);
			
			StorageManager.getInstance().commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public boolean addDeviceToAccount(final String serialNumber, final int userId) {
		try {
			JSONObject registryInfo = StorageManager.getInstance().getDeviceRegistryManager().getDeviceInfo(serialNumber);
			if (registryInfo == null) {
				return false;
			}
			
			JSONObject info = null;
			try {
				info = userDevicesStorage.getJSONObject(serialNumber);
			} catch (Exception e) {
			}
			if (info == null) {
				userDevicesStorage.put(serialNumber, new JSONObject());
			}

			info.put("userId", userId);
			
			userDevicesStorage.put(serialNumber, info);
			
			StorageManager.getInstance().commit();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public boolean removeDeviceFromAccount(final String serialNumber) {
		try {
			JSONObject info = userDevicesStorage.getJSONObject(serialNumber);
			if (info == null) {
				return false;
			}
	
			info.remove("userId");
			
			userDevicesStorage.put(serialNumber, info);
	
			StorageManager.getInstance().commit();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	public JSONObject getDeviceIds(final int userId) {
		try {
			JSONArray registeredIds = new JSONArray();
			JSONArray unregisteredIds = new JSONArray();

			Set<String> knownBssids = new HashSet<String>();
			for (Iterator<String> it = userDevicesStorage.keys(); it.hasNext(); ) {
				final String serialNumber = it.next();
				
				JSONObject info = userDevicesStorage.getJSONObject(serialNumber);
				
				int recordUserId = -1;
				try {
					recordUserId = info.getInt("userId");
				} catch (Exception e) {					
				}
				
				String recordBssid = null;
				try {
					recordBssid = info.getString("bssid");
				} catch (Exception e) {					
				}
				
				if (recordUserId == userId) {
					registeredIds.put(serialNumber);
					
					if (recordBssid != null) {
						knownBssids.add(recordBssid);						
					}
				}
			}
			
			for (Iterator<String> it = userDevicesStorage.keys(); it.hasNext(); ) {
				final String serialNumber = it.next();
				
				JSONObject info = userDevicesStorage.getJSONObject(serialNumber);
				
				
				int recordUserId = -1;
				try {
					recordUserId = info.getInt("userId");
				} catch (Exception e) {					
				}

				String recordBssid = null;
				try {
					recordBssid = info.getString("bssid");
				} catch (Exception e) {					
				}
				if (recordUserId == -1 && recordBssid != null && knownBssids.contains(recordBssid)) {
					unregisteredIds.put(serialNumber);
				}
			}

			JSONObject result = new JSONObject();
			result.put("registered", registeredIds);
			result.put("unregistered", unregisteredIds);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	


	
	public JSONObject getDeviceInfo(final String serialNumber) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(serialNumber);
			if (deviceInfo == null) {
				return null;
			}
			
			JSONObject registryInfo = StorageManager.getInstance().getDeviceRegistryManager().getDeviceInfo(serialNumber);
			if (registryInfo == null) {
				return null;
			}
			
			JSONObject result = new JSONObject();
			
			result.put("id", serialNumber);
			result.put("name", registryInfo.getString("name"));
			result.put("type", registryInfo.getString("type"));
			result.put("version", registryInfo.getString("version"));
			result.put("serial_number", serialNumber);
			
			
			String status = "offline";
			try {
				long updateTimestampt = deviceInfo.getLong("status_update_timestamp");
				if (System.currentTimeMillis() - updateTimestampt < STATUS_VALIDITY_TIMEOUT) {
					status = deviceInfo.getString("status");
				}				
			} catch(Exception e) {
			}
			result.put("status", status);
			
			String ipAddress = "";
			try {
				ipAddress = deviceInfo.getString("ip_address");
			} catch(Exception e) {
			}
			result.put("ip_address", ipAddress);
			
			result.put("schedule", getDeviceSchedule(serialNumber));
			result.put("mode", getDeviceMode(serialNumber));

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public JSONObject getDeviceSchedule(final String serialNumber) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(serialNumber);
			if (deviceInfo != null) {
				return deviceInfo.getJSONObject("schedule");
			}
		} catch (Exception e) {
		}
		
		try {
			String deviceType = StorageManager.getInstance().getDeviceRegistryManager().getDeviceInfo(serialNumber).getString("type");
			return StorageManager.getInstance().getDeviceRegistryManager().getDeviceSchedule(deviceType);			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public JSONObject setDeviceSchedule(final String serialNumber, final JSONObject schedule) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(serialNumber);
			if (deviceInfo == null) {
				return null;
			}
			
			deviceInfo.put("schedule", schedule);
			
			StorageManager.getInstance().commit();
			
			return schedule;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}	
	
	public String getDeviceMode(final String serialNumber) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(serialNumber);
			if (deviceInfo != null) {
				return deviceInfo.getString("mode");
			}
		} catch (Exception e) {
		}		

		try {
			String deviceType = StorageManager.getInstance().getDeviceRegistryManager().getDeviceInfo(serialNumber).getString("type");
			return StorageManager.getInstance().getDeviceRegistryManager().getDeviceMode(deviceType);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String setDeviceMode(final String deviceId, final String mode) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(deviceId);
			if (deviceInfo == null) {
				return null;
			}
			
			deviceInfo.put("mode", mode);
			
			StorageManager.getInstance().commit();
			
			return mode;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}	
	
	
	
	public JSONObject getDeviceProgramLibrary(final String serialNumber) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(serialNumber);
			if (deviceInfo == null) {
				return null;
			}
			
			return deviceInfo.getJSONObject("library");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public JSONObject setDeviceProgramLibrary(final String serialNumber, final JSONObject library) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(serialNumber);
			if (deviceInfo == null) {
				return null;
			}
			
			deviceInfo.put("library", library);
			
			StorageManager.getInstance().commit();
			
			return library;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
}

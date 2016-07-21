package com.piztec.hween.persistance;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {
 *   <device type>: {
 *     settings: {
 *       categories: [{data: String, display: String}],
 *       supportedCommands: [{data: String, display: String, description: String}],
 *       supportedProgramTriggers: [{data: String, display: String}],
 *       icon: null
 *     },
 *     schedule: {
 *     }
 *   },
 *   deviceRegistry: {
 *     <serialNumber>: {
 *       
 *     }
 *   }
 * }
 */

public class DeviceRegistryStorageManager {
	public final String DEVICE_TYPE_STUMP_GHOST = "stump_ghost";
	
	private final JSONObject deviceRegistryStorage;
	
	DeviceRegistryStorageManager(JSONObject deviceRegistryRoot) {
		deviceRegistryStorage = deviceRegistryRoot;
		
		String settings = "{\"categories\": [ {\"data\": \"fun\", \"display\": \"Fun\"}, {\"data\": \"scary\", \"display\": \"Scary\"} ],";
		
		settings += "\"supportedCommands\": [ {\"data\": \"reset\", \"display\": \"Reset\", \"description\": \"Sets toy to the initial position\"},";
		settings += "{\"data\": \"move_up\", \"display\": \"Move Up\", \"description\": \"Move toy up one inch\"},";
		settings += "{\"data\": \"move_down\", \"display\": \"Move Down\", \"description\": \"Move toy down one inch\"},";
		settings += "{\"data\": \"turn_left\", \"display\": \"Turn Left\", \"description\": \"Turn left a bit\"},";
		settings += "{\"data\": \"turn_right\", \"display\": \"Turn Right\", \"description\": \"Turn right a bit\"},";
		settings += "{\"data\": \"eyes_on\", \"display\": \"Turn Eyes On\", \"description\": \"Turn eyes on\"},";
		settings += "{\"data\": \"eyes_off\", \"display\": \"Turn Eyes Off\", \"description\": \"Turn eyes off\"},";
		settings += "{\"data\": \"talk\", \"display\": \"Speak\", \"description\": \"Say something\"},";
		settings += "{\"data\": \"pause\", \"display\": \"Do nothing\", \"description\": \"Do nothing for a bit\"} ],";
		
		settings += "\"supportedProgramTriggers\": [ {\"data\": \"immediately\", \"display\": \"Previous\"},";
		settings += "{\"data\": \"delay\", \"display\": \"Delay\"},";
		settings += "{\"data\": \"motion\", \"display\": \"Motion\"} ],";
		
		settings += "\"icon\": null}";
		
		
		String defaultSchedule = "{\"trigger\": \"motion\", \"programs\": []}";
		
		try {
			JSONObject deviceTypeObject = new JSONObject();
			deviceTypeObject.put("settings", new JSONObject(settings));
			deviceTypeObject.put("schedule", new JSONObject(defaultSchedule));
			deviceRegistryStorage.put(DEVICE_TYPE_STUMP_GHOST, deviceTypeObject);
			
			JSONObject devices = new JSONObject();
			String deviceInfo = "{\"serial_number\": \"0000000001\", \"verification_code\": 123456, \"type\": \"" + DEVICE_TYPE_STUMP_GHOST + "\", \"version\": \"1.0\", \"name\": \"Ghost-1\"}";
			devices.put("0000000001", new JSONObject(deviceInfo));
			deviceRegistryStorage.put("deviceRegistry", devices);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public JSONObject getDeviceSettings(final String deviceType) {
		try {
			JSONObject typeObject = deviceRegistryStorage.getJSONObject(deviceType);
			return typeObject.getJSONObject("settings");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONObject getDeviceSchedule(final String deviceType) {
		try {
			JSONObject typeObject = deviceRegistryStorage.getJSONObject(deviceType);
			return typeObject.getJSONObject("schedule");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONObject getDeviceInfo(final String serialNumber) {
		try {
			JSONObject registry = deviceRegistryStorage.getJSONObject("deviceRegistry");
			return registry.getJSONObject(serialNumber);
		} catch (JSONException e) {
			return null;
		}
	}
}

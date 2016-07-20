package com.piztec.hween.persistance;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {
 *   settings: {
 *     categories: [{data: String, display: String}],
 *     supportedCommands: [{data: String, display: String, description: String}],
 *     supportedProgramTriggers: [{data: String, display: String}],
 *     icon: null
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
		
		try {
			JSONObject deviceSettings = new JSONObject();
			deviceSettings.put(DEVICE_TYPE_STUMP_GHOST, new JSONObject(settings));			
			deviceRegistryStorage.put("settings", deviceSettings);
			
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
			return deviceRegistryStorage.getJSONObject(deviceType);
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

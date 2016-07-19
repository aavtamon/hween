package com.piztec.hween.persistance;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {
 *   categories: [{data: String, display: String}],
 *   supportedCommands: [{data: String, display: String, description: String}],
 *   supportedProgramTriggers: [{data: String, display: String}]
 * }
 */

public class DeviceSettingsStorageManager {
	public final String DEVICE_TYPE_STUMP_GHOST = "stump_ghost";
	
	private final JSONObject deviceSettingsStorage;
	
	DeviceSettingsStorageManager(JSONObject deviceSettingsRoot) {
		deviceSettingsStorage = deviceSettingsRoot;
		
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
		settings += "{\"data\": \"motion\", \"display\": \"Motion\"} ]}";
		
		try {
			deviceSettingsStorage.put(DEVICE_TYPE_STUMP_GHOST, new JSONObject(settings));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public JSONObject getDeviceSettings(final String deviceType) {
		try {
			return deviceSettingsStorage.getJSONObject(deviceType);
		} catch (JSONException e) {
			return null;
		}
	}
}

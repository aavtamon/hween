package com.piztec.hween.device;

import org.json.JSONException;
import org.json.JSONObject;

import com.piztec.hween.persistance.StorageManager;

public class DeviceUtils {
	public static boolean isAuthenticated(final String serialNumber, final String authHeader) {
		if (authHeader == null) {
			return false;
		}
		
		JSONObject info = StorageManager.getInstance().getDeviceRegistryManager().getDeviceInfo(serialNumber);
		if (info == null) {
			return false;
		}
		
		try {
			if (!info.getString("secret_word").equals(authHeader)) {
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
		
		return true;
	}
}

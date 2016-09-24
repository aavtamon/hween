package com.piztec.hween.persistance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.json.JSONException;
import org.json.JSONObject;


public class StorageManager {
	private static final StorageManager instance = new StorageManager();
	private static final String STORAGE_FILE_PATH = "storage.json";
	
	{
		System.out.println("Database full path = " + new File(STORAGE_FILE_PATH).getAbsolutePath());
	}
	
	private JSONObject storage;
	private UserAccountStorageManager userAccountStorageManager;
	private DeviceRegistryStorageManager deviceRegistryStorageManager;
	private DevicesStorageManager userDevicesStorageManager;
	private CodeUpgradeManager codeUpgradeManager;
	
	
	public static StorageManager getInstance() {
		return instance;
	}
	
	public UserAccountStorageManager getUserAccountManager() {
		return userAccountStorageManager;
	}
	
	public DevicesStorageManager getDevicesManager() {
		return userDevicesStorageManager;
	}
	
	public DeviceRegistryStorageManager getDeviceRegistryManager() {
		return deviceRegistryStorageManager;
	}

	public CodeUpgradeManager getCodeUpgradeManager() {
		return codeUpgradeManager;
	}

	
	
	public void commit() {
		if (storage == null) {
			System.err.println("Cannot commit a non-initialized database");
		}
		
		try {
			FileWriter fw = new FileWriter(STORAGE_FILE_PATH);
			storage.write(fw);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private StorageManager() {
		storage = StorageManager.readStorageFile();
		if (storage == null) {
			storage = initStorage();
		}
		
		try {
			userAccountStorageManager = new UserAccountStorageManager(storage.getJSONObject("userAccounts"));
			userDevicesStorageManager = new DevicesStorageManager(storage.getJSONObject("userDevices"));
			deviceRegistryStorageManager = new DeviceRegistryStorageManager(storage.getJSONObject("deviceSettings"));
			codeUpgradeManager = new CodeUpgradeManager();
			
			commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	private JSONObject initStorage() {
		String initialContent = "{\"userAccounts\": {}, \"deviceSettings\": {}, \"userDevices\": {}}";
		
		try {
			return new JSONObject(initialContent);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	private static JSONObject readStorageFile() {
	    try {
	        BufferedReader br = new BufferedReader(new FileReader(STORAGE_FILE_PATH));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            line = br.readLine();
	        }
	        br.close();
	        return new JSONObject(sb.toString());
	    } catch(Exception e) {
//	        e.printStackTrace();
	        return null;
	    }
	}	
}

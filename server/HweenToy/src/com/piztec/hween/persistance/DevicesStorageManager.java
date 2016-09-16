package com.piztec.hween.persistance;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * {
 *   <serial number>: {
 *     ip_address: <string>,
 *     port: <int>
 *     bssid: <string>,
 *     status: <stirng>,
 *     status_update_timestamp: <long>,
 *     user_id: <int>,
 *     
 *     schedule: {
 *       revision: <long>,
 *     
 *       trigger: <string>,
 *       programs: [
 *         {
 *           id: <long>,
 *           frequency: <string>,
 *         },
 *         ...
 *       ]
 *     },
 *     mode: <string>,
 *     
 *     library: {
 *       <id>: {
 *         revision: <long>,
 *       
 *         id: <long>,
 *         title: <string>,
 *         description: <string>,
 *         commands: [
 *           <string>,
 *           ...
 *         ]
 *       }
 *     }
 *   },
 *   ...
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
			info.put("port", reportedInfo.get("port"));
			
			Object bssid = null;
			try {
				bssid = reportedInfo.get("bssid");
			} catch (Exception e) {
			}
			info.put("bssid", bssid);
			
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
				info = new JSONObject();
			}

			info.put("user_id", userId);
			
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
	
			info.remove("user_id");
			
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
					recordUserId = info.getInt("user_id");
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
					recordUserId = info.getInt("user_id");
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
			
			int port = -1;
			try {
				port = deviceInfo.getInt("port");
			} catch(Exception e) {
			}
			result.put("port", port);
			
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
			JSONObject schedule = StorageManager.getInstance().getDeviceRegistryManager().getDeviceSchedule(deviceType);
			setDeviceSchedule(serialNumber, schedule);
			
			return schedule;
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

			schedule.put("revision", PersistanceUtils.generateRevision());
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
	
	
	
	public JSONObject getDeviceLibraryPrograms(final String serialNumber) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(serialNumber);
			if (deviceInfo == null) {
				return null;
			}
			
			try {
				JSONObject library = deviceInfo.getJSONObject("library");
				if (library != null) {
					return library;
				}
			} catch (Exception e) {				
			}
			
			return new JSONObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public JSONObject getDeviceLibraryProgram(final String serialNumber, final int programId) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(serialNumber);
			if (deviceInfo == null) {
				return null;
			}
			
			JSONObject library = deviceInfo.getJSONObject("library");
			if (library != null) {
				return library.getJSONObject(programId + "");
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}		
	}
	
	public int addLibraryProgram(final String serialNumber, final JSONObject program) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(serialNumber);
			if (deviceInfo == null) {
				return -1;
			}
			
			JSONObject library = null;
			try {
				library = deviceInfo.getJSONObject("library");
			} catch (Exception e) {				
			}
			
			if (library == null) {
				library = new JSONObject();
			}
			
			
			int id = PersistanceUtils.generateUniqueId();
			program.put("id", id);
			program.put("revision", PersistanceUtils.generateRevision());
			library.put(id + "", program);
			
			deviceInfo.put("library", library);
			
			StorageManager.getInstance().commit();

			return id;
		} catch (JSONException e) {
			return -1;
		}
	}	
	
	public JSONObject updateLibraryProgram(final String serialNumber, int id, final JSONObject program) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(serialNumber);
			if (deviceInfo == null) {
				return null;
			}
			
			JSONObject library = null;
			try {
				library = deviceInfo.getJSONObject("library");
			} catch (Exception e) {				
			}
			
			if (library == null) {
				return null;
			}
			
			
//			JSONObject oldProgram = null;
//			try {
//				oldProgram = library.getJSONObject(id + "");
//				
//				
//			} catch (Exception e) {
//				oldProgram = program;
//			}
			
			program.put("revision", PersistanceUtils.generateRevision());
			library.put(id + "", program);
			
			deviceInfo.put("library", library);
			
			StorageManager.getInstance().commit();

			return program;
		} catch (JSONException e) {
			return null;
		}
	}	

	public JSONObject removeLibraryProgram(final String serialNumber, final int programId) {
		try {
			JSONObject deviceInfo = userDevicesStorage.getJSONObject(serialNumber);
			if (deviceInfo == null) {
				return null;
			}

			JSONObject library = deviceInfo.getJSONObject("library");
			library.remove(programId + "");
			
			deviceInfo.put("library", library);
			
			JSONObject schedule = getDeviceSchedule(serialNumber);
			if (schedule != null) {
				JSONArray updatedPrograms = new JSONArray();
				JSONArray programs = schedule.getJSONArray("programs");
				for (int i = 0; i < programs.length(); i++) {
					JSONObject program = programs.getJSONObject(i);
					long deviceProgramId = program.getLong("id");
					if (deviceProgramId != programId) {
						updatedPrograms.put(program);
					}
				}
				schedule.put("programs", updatedPrograms);
				
				setDeviceSchedule(serialNumber, schedule);
			}

			StorageManager.getInstance().commit();

			return library;
		} catch (JSONException e) {
			return null;
		}
	}	
}

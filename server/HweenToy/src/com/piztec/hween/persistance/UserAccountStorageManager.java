package com.piztec.hween.persistance;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {
 *   profile: {
 *     user_id: int,
 *     login: String,
 *     password: String
 *   },
 *   preferences: {
 *   },
 *   settings: {
 *   }
 * }
 */

public class UserAccountStorageManager {
	private final JSONObject userAccountsStorage;
	
	UserAccountStorageManager(JSONObject userAccountsRoot) {
		userAccountsStorage = userAccountsRoot;
	}
	
	
	public int getUserId(final String login) {
		try {
			for (Iterator it = userAccountsStorage.keys(); it.hasNext(); ) {
				String key = it.next().toString();
				JSONObject account = userAccountsStorage.getJSONObject(key);
				
				JSONObject profile = account.getJSONObject("profile");
				String accountLogin = profile.getString("login");
				if (accountLogin.equals(login)) {
					return profile.getInt("user_id");
				}
			}
			return -1;
		} catch (Exception e) {
			return -1;
		}		
	}
	
	public JSONObject getUserAccount(final int userId) {
		try {
			return userAccountsStorage.getJSONObject(userId + "");
		} catch (Exception e) {
			return null;
		}		
	}
	
	public JSONObject getUserProfile(final int userId) {
		JSONObject userAccount = getUserAccount(userId);
		if (userAccount != null) {
			try {
				return userAccount.getJSONObject("profile");
			} catch (JSONException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	public JSONObject getUserPreferences(final int userId) {
		JSONObject userAccount = getUserAccount(userId);
		if (userAccount != null) {
			try {
				return userAccount.getJSONObject("preferences");
			} catch (JSONException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	public JSONObject getUserSettings(final int userId) {
		JSONObject userAccount = getUserAccount(userId);
		if (userAccount != null) {
			try {
				return userAccount.getJSONObject("settings");
			} catch (JSONException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public int createUserAccount(final JSONObject profile) {
		try {
			JSONObject userAccount = new JSONObject();
			
			int userId = (int)System.currentTimeMillis();
			profile.put("user_id", userId);
			
			userAccount.put("profile", profile);
			userAccount.put("preferences", new JSONObject());
			userAccount.put("settings", new JSONObject());
			
			userAccountsStorage.put(userId + "", userAccount);
			
			StorageManager.getInstance().commit();
			
			return userId;
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
	}
}

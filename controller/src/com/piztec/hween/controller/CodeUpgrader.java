package com.piztec.hween.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class CodeUpgrader {
	static class Upgrade {
		public String version;
		public String schedule;
		public String image;
	}
	
	private static final String VERSION_FILE = "version.json";
	
	private static CodeUpgrader instance;
	
	public static CodeUpgrader getInstance() {
		if (instance == null) {
			instance = new CodeUpgrader();
		}
		
		return instance;
	}
	
	private CodeUpgrader() {
	}
	
	public void upgrade(final Upgrade upgrade) {
		String currentVersion = getCurrentImageVersion();
		if (currentVersion != null && currentVersion.equals(upgrade.version)) {
			System.out.println("Current version muches the upgrade - nothing to do");
			return;
		}
		
		try {
			byte[] image = Base64.getDecoder().decode(upgrade.image);
			
		} catch (IllegalArgumentException iae) {
			System.err.println("Invalid image. Image version " + upgrade.image);
		}
	}
	
	
	public String getCurrentImageVersion() {
		String versionFileString = new Scanner(CodeUpgrader.class.getResourceAsStream(VERSION_FILE)).useDelimiter("\\A").next();
		try {
			JSONObject versionObject = new JSONObject(versionFileString);
			
			return versionObject.getString("version");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

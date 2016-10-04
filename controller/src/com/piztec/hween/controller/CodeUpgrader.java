package com.piztec.hween.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
	
	private final File workingDirectory;
	
	public static CodeUpgrader getInstance() {
		if (instance == null) {
			instance = new CodeUpgrader();
		}
		
		return instance;
	}
	
	private CodeUpgrader() {
		workingDirectory = new File(System.getProperty("user.dir"), "upgrade_tmp");
		System.out.println("Code upgrade directory = "+ workingDirectory);
		
		if (!workingDirectory.isDirectory()) {
			if (workingDirectory.exists()) {
				workingDirectory.delete();
			}
			workingDirectory.mkdir();
		} else {
			clearWorkingDir();
		}		
	}
	
	public boolean upgrade(final Upgrade upgrade) {
		String currentVersion = getCurrentImageVersion();
		if (currentVersion != null && currentVersion.equals(upgrade.version)) {
			System.out.println("Current version muches the upgrade - nothing to do");
			return false;
		}
		
		try {
			byte[] image = Base64.getDecoder().decode(upgrade.image);
			System.out.println("New image is received. Image size is " + image.length);
			
			clearWorkingDir();
			
			try {
				installImage(image);
				return true;
			} catch (IOException ioe) {
				System.err.println("Failed to install an image");
				ioe.printStackTrace();
			}
		} catch (IllegalArgumentException iae) {
			System.err.println("Invalid image. Image version " + upgrade.image);
		}
		
		return false;
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
	
	private void clearWorkingDir() {
		purgeDirectory(workingDirectory);
	}
	
	// The expected structure of the image archive:
	// install.sh
	// controller.tar
	// run_controller.sh
	// system/hween.service
	private void installImage(final byte[] image) throws IOException {
		// Create image on the disk
		FileOutputStream fos = new FileOutputStream(new File(workingDirectory, "image.tar"));
		fos.write(image);
		fos.close();

		File unpackScriptFile = new File(workingDirectory, "unpack.sh");
		System.out.println("Creating installable image in " + workingDirectory.getAbsolutePath());
		
		PrintWriter writer = new PrintWriter(unpackScriptFile);
		writer.println("# Auto-generated installer script");
		writer.println("cd " + workingDirectory.getAbsolutePath());
		writer.println("tar -xvf image.tar");
		writer.println("cd image");
		writer.println("sudo sh ./install.sh");
		writer.close();
		
		System.out.println("Executing installation script " + unpackScriptFile.getAbsolutePath());
		Runtime.getRuntime().exec("sh " + unpackScriptFile.getAbsolutePath());
	}
	
	private void purgeDirectory(final File dir) {
	    for (File file: dir.listFiles()) {
	        if (file.isDirectory()) {
	        	purgeDirectory(file);
	        }
	        file.delete();
	    }
	}	
}

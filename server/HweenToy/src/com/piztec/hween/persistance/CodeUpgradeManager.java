package com.piztec.hween.persistance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


public class CodeUpgradeManager {
	private static final String DESCRIPTOR_DIRECTORY = "/Users/aavtamonov/project/other/hween/server/code_images";
	
	private Map<String, JSONObject> imageDescriptors = new HashMap<String, JSONObject>();
	
	CodeUpgradeManager() {
		rehash();
	}
	
	public String getVersion(final String serialNumber) {
		JSONObject descriptor = getImageDescriptor(serialNumber);
		if (descriptor == null) {
			return null;
		}
		
		try {
			return descriptor.getString("version");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] getImage(final String serialNumber) {
		JSONObject descriptor = getImageDescriptor(serialNumber);
		if (descriptor == null) {
			return null;
		}
		
		try {
			String imageName = descriptor.getString("image_file");
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			File imageFile = new File(imageName);
			if (!imageFile.isAbsolute()) {
				imageFile = new File(DESCRIPTOR_DIRECTORY, imageName);
			}
			InputStream is = new FileInputStream(imageFile);
			int nextByte;
			while ((nextByte = is.read()) != -1) {
				output.write(nextByte);
			}
			is.close();
			output.close();
			
			return output.toByteArray();			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getBase64Image(final String serialNumber) {
		byte[] image = getImage(serialNumber);
		if (image != null) {
			return Base64.getEncoder().encodeToString(image);
		} else {
			return null;
		}
	}

	private JSONObject getImageDescriptor(final String serialNumber) {
		JSONObject registryInfo = StorageManager.getInstance().getDeviceRegistryManager().getDeviceInfo(serialNumber);
		if (registryInfo == null) {
			return null;
		}
		
		String deviceType = null;
		try {
			deviceType = registryInfo.getString("type");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONObject descriptor = getDescriptor(deviceType, serialNumber);
		if (descriptor == null) {
			System.err.println("No image for the device " + deviceType + ", serial number " + serialNumber);
		}
		return descriptor;
	}
	
	private JSONObject getDescriptor(final String deviceType, final String serialNumber) {
		JSONObject descriptor = imageDescriptors.get("descriptor-" + deviceType + "-" + serialNumber + ".json");
		if (descriptor != null) {
			return descriptor;
		}
			
		return imageDescriptors.get("descriptor-" + deviceType + ".json");
	}
	
	
	private void rehash() {
		imageDescriptors.clear();
		
		File descriptorDirectory = new File(DESCRIPTOR_DIRECTORY);
		File[] files = descriptorDirectory.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".json");
			}
		});
		
		for (File file : files) {
			String fileContent = PersistanceUtils.readFile(file.getAbsolutePath());
			if (fileContent != null) {
				try {
					imageDescriptors.put(file.getName(), new JSONObject(fileContent));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

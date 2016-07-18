package com.piztec.hween.persistance;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class DeviceSettings {
	public static final String STUMP_GHOST_TYPE = "stump_ghost";
	
	
	@Id
	private String deviceType;

	@OneToMany
	private List<DisplayableElement> categories;

	@OneToMany
	private List<DisplayableElement> supportedCommands;

	@OneToMany
	private List<DisplayableElement> supportedProgramTriggers;
	

	public DeviceSettings() {
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String type) {
		this.deviceType = type;
	}

	public List<DisplayableElement> getCategories() {
		return categories;
	}
	public void setCategories(List<DisplayableElement> categories) {
		this.categories = categories;
		
		for (DisplayableElement category : categories) {
			category.setDeviceType(deviceType);
		}
	}
	
	public List<DisplayableElement> getSupportedCommands() {
		return supportedCommands;
	}
	public void setSupportedCommands(List<DisplayableElement> supportedCommands) {
		this.supportedCommands = supportedCommands;
		
		for (DisplayableElement command : supportedCommands) {
			command.setDeviceType(deviceType);
		}
	}
	
	public List<DisplayableElement> getSupportedProgramTriggers() {
		return supportedProgramTriggers;
	}
	public void setSupportedProgramTriggers(List<DisplayableElement> triggers) {
		this.supportedProgramTriggers = triggers;
		
		for (DisplayableElement trigger : supportedProgramTriggers) {
			trigger.setDeviceType(deviceType);
		}
	}
	

	@Override
	public String toString() {
		StringBuilder catBuilder = new StringBuilder();
		for (DisplayableElement category : categories) {
			catBuilder.append(category);
		}

		StringBuilder commandBuilder = new StringBuilder();
		for (DisplayableElement command : supportedCommands) {
			commandBuilder.append(command);
		}

		StringBuilder triggerBuilder = new StringBuilder();
		for (DisplayableElement trigger : supportedProgramTriggers) {
			triggerBuilder.append(trigger);
		}

		return "{\"categories\": [" + catBuilder.toString() + "], \"supportedCommands\": [" + commandBuilder.toString() + "], \"supportedProgramTriggers\": [" + triggerBuilder.toString() + "]}";
	}
}

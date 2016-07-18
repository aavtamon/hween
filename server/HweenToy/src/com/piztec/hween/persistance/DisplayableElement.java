package com.piztec.hween.persistance;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class DisplayableElement {
	@Id
	private String data;
	private String display;
	private String description;
	
	private String deviceType;

	public DisplayableElement() {
	}

	public DisplayableElement(String data, String display) {
		this(data, display, "default");
	}
	
	public DisplayableElement(String data, String display, String description) {
		this.data = data;
		this.display = display;
		this.description = description;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getDecription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	
	@Override
  	public String toString() {
		return "{\"data\": \"" + this.data + "\", \"display\": \"" + this.display + "\", \"description\": \"" + this.description + "\"}";
    }
}

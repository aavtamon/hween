package com.piztec.hween.controller.drivers;

import java.util.ArrayList;
import java.util.List;

public abstract class DeviceDriver {
	private List<DeviceEventListener> deviceListeners = new ArrayList<DeviceEventListener>();
	
	public abstract class Command {
		private final String name;
		
		Command(final String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public String toString() {
			return name;
		}
		
		public abstract boolean execute(final Object param) throws Exception;
	}
	
	public abstract Command getCommand(final String commandName);
	
	
	public static class Trigger {
		public interface TriggerListener {
			public void onTriggerEvent();
		}
		
		private final List<TriggerListener> listeners = new ArrayList<DeviceDriver.Trigger.TriggerListener>();
		private final String name;
		
		Trigger(final String name) {
			this.name = name;
		}
		
		public void addTriggerListener(final TriggerListener listener) {
			listeners.add(listener);
		}
		
		public void removeTriggerListener(final TriggerListener listener) {
			listeners.remove(listener);
		}
		
		public String getName() {
			return name;
		}
		
		void notifyListeners() {
			for (TriggerListener l : listeners) {
				l.onTriggerEvent();
			}
		}
	}
	
	public abstract Trigger getTrigger(final String name);
	
	
	
	public interface DeviceEventListener {
		public String WPS_CONNECT = "wps_connect";
		public String DISCONNECT = "disconnect";
		
		public void onDeviceEvent(final String eventType);
	}
	
	public void addDviceEventListener(final DeviceEventListener listener) {
		deviceListeners.add(listener);
	}

	public void removeDviceEventListener(final DeviceEventListener listener) {
		deviceListeners.remove(listener);
	}
	
	void notifyEventListener(final String eventType) {
		for (DeviceEventListener listener: deviceListeners) {
			listener.onDeviceEvent(eventType);
		}
	}
}

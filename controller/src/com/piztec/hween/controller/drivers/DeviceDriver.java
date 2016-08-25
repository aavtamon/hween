package com.piztec.hween.controller.drivers;

import java.util.ArrayList;
import java.util.List;

public interface DeviceDriver {
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
	
	public Command getCommand(final String commandName);
	
	
	public class Trigger {
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
	
	public Trigger getTrigger(final String name);
}

package com.piztec.hween.controller.drivers;

import java.util.ArrayList;
import java.util.List;

public abstract class DeviceDriver {
	public static String INDICATOR_WPS = "wps";
	public static String INDICATOR_NETWORK = "network";
	public static String INDICATOR_STATUS = "status";
	
	public static String BUTTON_WPS = "wps";
	

	public static abstract class Command {
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
	
	
	
	// Device Controls
	
	public static abstract class Indicator {
		private String name;
		private Thread blinkingThread;
		
		Indicator(final String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void turnOn() {
			stopBlinkingThread();

			set(true);
		}
		
		public void turnOff() {
			stopBlinkingThread();

			set(false);
		}
		
		public void blink(final int period) {
			stopBlinkingThread();
			
			blinkingThread = new Thread() {
				private boolean state = true;
				
				public void run() {
					while (!isInterrupted()) {
						set(state);

						try {
							Thread.sleep(period);
						} catch (InterruptedException e) {
							break;
						}
						
						state = !state;
					}
					
					blinkingThread = null;
				}
			};
			blinkingThread.start();
		}
		
		
		protected abstract void setState(final boolean on);
		protected abstract boolean getState();
		
		private void set(final boolean on) {
			if (getState() != on) {
				setState(on);
			}
		}
		
		private void stopBlinkingThread() {
			if (blinkingThread != null) {
				blinkingThread.interrupt();
				try {
					blinkingThread.join();
				} catch (InterruptedException e) {
				}
				blinkingThread = null;
			}
		}
	}
	
	
	public abstract Indicator getIndicator(String name);
	
	
	public static abstract class Button {
		public interface ButtonListener {
			public void onPressed();
		}
		
		private List<ButtonListener> buttonListeners = new ArrayList<ButtonListener>();
		private String name;
		
		Button(final String name) {
			this.name = name;
			register();
		}
		
		public String getName() {
			return name;
		}

		public void addListener(final ButtonListener listener) {
			buttonListeners.add(listener);
		}

		public void removeListener(final ButtonListener listener) {
			buttonListeners.remove(listener);
		}
		
		protected void notifyListeners() {
			for (ButtonListener listener: buttonListeners) {
				listener.onPressed();
			}
		}
		
		public abstract void register();
	}
	
	
	public abstract Button getButton(String name);
}

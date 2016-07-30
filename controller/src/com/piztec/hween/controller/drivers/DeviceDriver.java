package com.piztec.hween.controller.drivers;

public interface DeviceDriver {
	public abstract class Command {
		private final String name;
		
		public Command(final String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public String toString() {
			return name;
		}
		
		public abstract boolean execute(final Object param);
	}
	
	public Command getCommand(final String commandName);
}

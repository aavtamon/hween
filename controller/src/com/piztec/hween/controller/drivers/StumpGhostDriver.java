package com.piztec.hween.controller.drivers;

import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class StumpGhostDriver implements DeviceDriver {
	private Map<String, Command> commands = new HashMap<String, Command>();

	private GpioPinDigitalOutput pin1; 
	private GpioPinDigitalOutput pin2; 
	private GpioPinDigitalOutput pin3; 
	private GpioPinDigitalOutput pin4;
	
	
	public StumpGhostDriver() {
//		initPins();
		initCommands();
	}
	
	private void initPins() {
		GpioController gpioController = GpioFactory.getInstance();
		
		pin1 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, "pin1", PinState.LOW);
		pin1.setShutdownOptions(true, PinState.LOW);
		
		pin2 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02, "pin2", PinState.LOW);
		pin2.setShutdownOptions(true, PinState.LOW);

		pin3 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03, "pin3", PinState.LOW);
		pin3.setShutdownOptions(true, PinState.LOW);
		
		pin4 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_04, "pin4", PinState.LOW);
		pin4.setShutdownOptions(true, PinState.LOW);
	}
	
	
	private void initCommands() {
		commands.put("reset", new Command("reset") {
			public boolean execute(Object param) {
				return false;
			}
		});
		commands.put("move_up", new Command("move_up") {
			public boolean execute(Object param) {
				return false;
			}
		});
		commands.put("move_down", new Command("move_down") {
			public boolean execute(Object param) {
				return false;
			}
		});
		commands.put("turn_left", new Command("turn_left") {
			public boolean execute(Object param) {
				return false;
			}
		});
		commands.put("turn_right", new Command("turn_right") {
			public boolean execute(Object param) {
				return false;
			}
		});
		commands.put("eyes_on", new Command("eyes_on") {
			public boolean execute(Object param) {
				return false;
			}
		});
		commands.put("eyes_off", new Command("eyes_off") {
			public boolean execute(Object param) {
				return false;
			}
		});
		commands.put("talk", new Command("talk") {
			public boolean execute(Object param) {
				return false;
			}
		});
		commands.put("pause", new Command("pause") {
			public boolean execute(Object param) {
				return false;
			}
		});	
	}
	
	
	
	public Command getCommand(final String commandName) {
		return commands.get(commandName);
	}
}

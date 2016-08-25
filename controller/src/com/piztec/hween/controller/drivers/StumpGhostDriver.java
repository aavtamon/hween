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
	private Map<String, Trigger> triggers = new HashMap<String, Trigger>();

	private GpioPinDigitalOutput pin1; 
	private GpioPinDigitalOutput pin2; 
	private GpioPinDigitalOutput pin3; 
	
	
	public StumpGhostDriver() {
		initPins();
		initCommands();
		initTriggers();
	}
	
	private void initPins() {
		GpioController gpioController = GpioFactory.getInstance();
		
		pin1 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_10, "pin1", PinState.LOW);
		pin1.setShutdownOptions(true, PinState.LOW);
		
		pin2 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_17, "pin2", PinState.LOW);
		pin2.setShutdownOptions(true, PinState.LOW);

		pin3 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_21, "pin3", PinState.LOW);
		pin3.setShutdownOptions(true, PinState.LOW);
	}
	
	
	private void initCommands() {
		commands.put("reset", new Command("reset") {
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <reset> command: pin1");
				pin1.high();
				Thread.sleep(5000);

				pin1.low();
				System.out.println("Stump Ghost: <reset> command - completed");
				return false;
			}
		});
		commands.put("move_up", new Command("move_up") {
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <move up> command: pin2");
				pin2.high();
				Thread.sleep(5000);
				
				pin2.low();
				System.out.println("Stump Ghost: <move up> command - completed");
				return false;
			}
		});
		commands.put("move_down", new Command("move_down") {
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <move down> command: pin3");
				pin3.high();
				Thread.sleep(5000);
				
				pin3.low();
				System.out.println("Stump Ghost: <move down> command - completed");
				return false;
			}
		});
		commands.put("turn_left", new Command("turn_left") {
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <turn left> command");
				Thread.sleep(2000);
				System.out.println("Stump Ghost: <turn left> command - completed");
				
				return false;
			}
		});
		commands.put("turn_right", new Command("turn_right") {
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <turn right> command");
				Thread.sleep(1000);
				
				System.out.println("Stump Ghost: <turn right> command - completed");
				return false;
			}
		});
		commands.put("eyes_on", new Command("eyes_on") {
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <eyes on> command");
				Thread.sleep(100);
				
				System.out.println("Stump Ghost: <eyes on> command - completed");
				return false;
			}
		});
		commands.put("eyes_off", new Command("eyes_off") {
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <eyes off> command");
				Thread.sleep(100);
				
				System.out.println("Stump Ghost: <eyes off> command - completed");
				return false;
			}
		});
		commands.put("talk", new Command("talk") {
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <talk> command with param: " + param);
				Thread.sleep(100);
				
				System.out.println("Stump Ghost: <talk> command - completed");
				return false;
			}
		});
		commands.put("pause", new Command("pause") {
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <pause> command");
				Thread.sleep(3000);
				
				System.out.println("Stump Ghost: <pause> command - completed");
				return false;
			}
		});	
	}
	
	
	private void initTriggers() {
		triggers.put("motion", new Trigger("motion"));
	}
	
	
	public Command getCommand(final String commandName) {
		return commands.get(commandName);
	}

	public Trigger getTrigger(String name) {
		return triggers.get(name);
	}
}

package com.piztec.hween.controller.drivers;

import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.piztec.hween.controller.DeviceManager;

public class StumpGhostDriver implements DeviceDriver {
	private Map<String, Command> commands = new HashMap<String, Command>();
	private Map<String, Trigger> triggers = new HashMap<String, Trigger>();

	//http://pi4j.com/pins/model-b-rev1.html
	private GpioPinDigitalOutput out0_pin11; 
	private GpioPinDigitalOutput out3_pin15; 
	private GpioPinDigitalInput in12_pin19; 
	private GpioPinDigitalInput in13_pin21;
	
	private Object syncer = new Object();
	
	
	public StumpGhostDriver() {
		initPins();
		initCommands();
		initTriggers();
	}
	
	private void initPins() {
		if (DeviceManager.deviceFeaturesDisabled()) {
			return;
		}
		
		GpioController gpioController = GpioFactory.getInstance();
		
		out0_pin11 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, "Move Up", PinState.LOW);
		out0_pin11.setShutdownOptions(true, PinState.LOW);
		
		out3_pin15 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Move Down", PinState.LOW);
		out3_pin15.setShutdownOptions(true, PinState.LOW);

		in12_pin19 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_12, "Down Limit", PinPullResistance.PULL_DOWN);
		in12_pin19.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println("Pin 12 got an event");
			}
		});

		in13_pin21 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_13, "Up Limit", PinPullResistance.PULL_DOWN);
		in13_pin21.setShutdownOptions(true, PinState.LOW);
		in13_pin21.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println("Pin 13 got an event");
			}
		});
	}
	
	
	private void initCommands() {
		commands.put("reset", new Command("reset") {
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <reset> command");
				
				Thread.sleep(5000);

				System.out.println("Stump Ghost: <reset> command - completed");
				return false;
			}
		});
		commands.put("move_up", new Command("move_up") {
			public boolean execute(Object param) throws Exception {
				synchronized (syncer) {
					System.out.println("Stump Ghost: <move up> command");
					
					out0_pin11.high();
					Thread.sleep(5000);				
					out0_pin11.low();
					
					System.out.println("Stump Ghost: <move up> command - completed");
				}
				return false;
			}
		});
		commands.put("move_down", new Command("move_down") {
			public boolean execute(Object param) throws Exception {
				synchronized (syncer) {
					System.out.println("Stump Ghost: <move down> command");
					
					out3_pin15.high();
					Thread.sleep(5000);				
					out3_pin15.low();
					
					System.out.println("Stump Ghost: <move down> command - completed");
				}
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

				Thread.sleep(2000);
				
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

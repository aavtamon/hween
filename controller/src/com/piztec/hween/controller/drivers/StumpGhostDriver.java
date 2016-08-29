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

public class StumpGhostDriver extends DeviceDriver {
	private Map<String, Command> commands = new HashMap<String, Command>();
	private Map<String, Trigger> triggers = new HashMap<String, Trigger>();

	//http://pi4j.com/pins/model-b-rev1.html
	private GpioPinDigitalOutput upPin; 
	private GpioPinDigitalOutput downPin; 
	private GpioPinDigitalInput in12_pin19; 
	private GpioPinDigitalInput in13_pin21;
	
	private Object commandSyncer = new Object();
	private Object triggerSyncer = new Object();
	
	private boolean highLimitTrigger;
	private boolean lowLimitTrigger;
	
	private final boolean targetDeviceFeaturesEnabled;
	
	
	public StumpGhostDriver(final boolean targetDeviceFeaturesEnabled) {
		this.targetDeviceFeaturesEnabled = targetDeviceFeaturesEnabled;
		
		initPins();
		initCommands();
		initTriggers();
	}
	
	private void initPins() {
		if (!targetDeviceFeaturesEnabled) {
			return;
		}
		
		GpioController gpioController = GpioFactory.getInstance();
		
		upPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, "Move Up", PinState.LOW);
		upPin.setShutdownOptions(true, PinState.LOW);
		
		downPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Move Down", PinState.LOW);
		downPin.setShutdownOptions(true, PinState.LOW);

		in12_pin19 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_12, "Down Limit", PinPullResistance.PULL_DOWN);
		in12_pin19.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				synchronized (triggerSyncer) {
					highLimitTrigger = event.getState() == PinState.HIGH;
					triggerSyncer.notify();
				}
				
				System.out.println("Pin 12 got an event");
			}
		});

		in13_pin21 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_13, "Up Limit", PinPullResistance.PULL_DOWN);
		in13_pin21.setShutdownOptions(true, PinState.LOW);
		in13_pin21.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				synchronized (triggerSyncer) {
					lowLimitTrigger = event.getState() == PinState.HIGH;
					triggerSyncer.notify();
				}

				System.out.println("Pin 13 got an event");
			}
		});
		
		// Add notifyDeviceEvent for WPS and LED controls
	}
	
	
	private void initCommands() {
		commands.put("reset", new Command("reset") {
			public boolean execute(Object param) throws Exception {
				synchronized (commandSyncer) {
					synchronized (triggerSyncer) {
						System.out.println("Stump Ghost: <reset> command");
						
						if (lowLimitTrigger) {
							System.out.println("Stump Ghost: <reset> command cannot be executed - already low position");
							return false;
						}
						
						downPin.high();
						
						while (true) {
							triggerSyncer.wait(10000);
							if (lowLimitTrigger) {
								break;
							}
						}
						
						downPin.low();
						System.out.println("Stump Ghost: <reset> command - completed");
					}							
				}

				return true;
			}
		});
		commands.put("move_up", new Command("move_up") {
			public boolean execute(Object param) throws Exception {
				synchronized (commandSyncer) {
					synchronized (triggerSyncer) {
						System.out.println("Stump Ghost: <move up> command");
					
						if (highLimitTrigger) {
							System.out.println("Stump Ghost: <move up> command cannot be executed - already uphigh position");
							return false;
						}
						
						upPin.high();
						
						triggerSyncer.wait(1000);
						
						upPin.low();
						
						System.out.println("Stump Ghost: <move up> command - completed");
					}
				}
				
				return true;
			}
		});
		commands.put("move_down", new Command("move_down") {
			public boolean execute(Object param) throws Exception {
				synchronized (commandSyncer) {
					synchronized (triggerSyncer) {
						System.out.println("Stump Ghost: <move down> command");
					
						if (lowLimitTrigger) {
							System.out.println("Stump Ghost: <move down> command cannot be executed - already uphigh position");
							return false;
						}
						
						downPin.high();
						
						triggerSyncer.wait(1000);
						
						downPin.low();
						
						System.out.println("Stump Ghost: <move down> command - completed");
					}
				}
				
				return true;
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

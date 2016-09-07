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
	private Map<String, Indicator> indicators = new HashMap<String, Indicator>();
	private Map<String, Button> buttons = new HashMap<String, Button>();

	//http://pi4j.com/pins/model-b-rev1.html
	private GpioPinDigitalOutput upMotorOut; 
	private GpioPinDigitalOutput downMotorOut;
	private GpioPinDigitalOutput wpsLedOut;
	private GpioPinDigitalInput downMotorSwitchIn; 
	private GpioPinDigitalInput upMotorSwitchIn;
	private GpioPinDigitalInput wpsButtonIn;
	
	private Object commandSyncer = new Object();
	private Object triggerSyncer = new Object();
	
	private boolean upLimitTrigger;
	private boolean downLimitTrigger;
	
	private GpioController gpioController;
	
	public StumpGhostDriver(final boolean targetDeviceFeaturesEnabled) {
		if (targetDeviceFeaturesEnabled) {
			gpioController = GpioFactory.getInstance();
		}
		
		initCommands();
		initTriggers();
		initIndicators();
		initButtons();
	}
	
	
	private void initCommands() {
		commands.put("reset", new Command("reset") {
			protected void register() {
			}
			
			public boolean execute(Object param) throws Exception {
				synchronized (commandSyncer) {
					synchronized (triggerSyncer) {
						System.out.println("Stump Ghost: <reset> command");
						
						if (downLimitTrigger) {
							System.out.println("Stump Ghost: <reset> command cannot be executed - already low position");
							return false;
						}
						
						downMotorOut.high();
						
						while (true) {
							triggerSyncer.wait(10000);
							if (downLimitTrigger) {
								break;
							}
						}
						
						downMotorOut.low();
						System.out.println("Stump Ghost: <reset> command - completed");
					}							
				}

				return true;
			}
		});
		commands.put("move_up", new Command("move_up") {
			protected void register() {
				if (gpioController == null) {
					return;
				}
				
				upMotorOut = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, "Move Up", PinState.LOW);
				upMotorOut.setShutdownOptions(true, PinState.LOW);

				upMotorSwitchIn = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_13, "Up Limit", PinPullResistance.PULL_DOWN);
				upMotorSwitchIn.setShutdownOptions(true, PinState.LOW);
				upLimitTrigger = upMotorSwitchIn.getState() == PinState.HIGH;
				upMotorSwitchIn.addListener(new GpioPinListenerDigital() {
					public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
						System.out.println("Up Motor Switch got an event. State = " + event.getState());
						
						synchronized (triggerSyncer) {
							upLimitTrigger = event.getState() == PinState.HIGH;
							triggerSyncer.notify();
						}
					}
				});
			}			
			
			public boolean execute(Object param) throws Exception {
				synchronized (commandSyncer) {
					synchronized (triggerSyncer) {
						System.out.println("Stump Ghost: <move up> command");
					
						if (upLimitTrigger) {
							System.out.println("Stump Ghost: <move up> command cannot be executed - already uphigh position");
							return false;
						}
						
						upMotorOut.high();
						
						triggerSyncer.wait(1000);
						
						upMotorOut.low();
						
						System.out.println("Stump Ghost: <move up> command - completed");
					}
				}
				
				return true;
			}
		});
		commands.put("move_down", new Command("move_down") {
			protected void register() {
				if (gpioController == null) {
					return;
				}
				
				downMotorOut = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Move Down", PinState.LOW);
				downMotorOut.setShutdownOptions(true, PinState.LOW);
				downLimitTrigger = downMotorOut.getState() == PinState.HIGH;
				
				downMotorSwitchIn = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_12, "Down Limit", PinPullResistance.PULL_DOWN);
				downMotorSwitchIn.addListener(new GpioPinListenerDigital() {
					public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
						System.out.println("Down Motor Switch got an event. State = " + event.getState());
						
						synchronized (triggerSyncer) {
							downLimitTrigger = event.getState() == PinState.HIGH;
							triggerSyncer.notify();
						}
					}
				});
			}
			
			public boolean execute(Object param) throws Exception {
				synchronized (commandSyncer) {
					synchronized (triggerSyncer) {
						System.out.println("Stump Ghost: <move down> command");
					
						if (downLimitTrigger) {
							System.out.println("Stump Ghost: <move down> command cannot be executed - already lowest down position");
							return false;
						}
						
						downMotorOut.high();
						
						triggerSyncer.wait(1000);
						
						downMotorOut.low();
						
						System.out.println("Stump Ghost: <move down> command - completed");
					}
				}
				
				return true;
			}
		});
		commands.put("turn_left", new Command("turn_left") {
			protected void register() {				
			}
			
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <turn left> command");
				
				Thread.sleep(2000);
				
				System.out.println("Stump Ghost: <turn left> command - completed");
				return false;
			}
		});
		commands.put("turn_right", new Command("turn_right") {
			protected void register() {				
			}
			
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <turn right> command");

				Thread.sleep(2000);
				
				System.out.println("Stump Ghost: <turn right> command - completed");
				return false;
			}
		});
		commands.put("eyes_on", new Command("eyes_on") {
			protected void register() {				
			}
			
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <eyes on> command");
				
				Thread.sleep(100);
				
				System.out.println("Stump Ghost: <eyes on> command - completed");
				return false;
			}
		});
		commands.put("eyes_off", new Command("eyes_off") {
			protected void register() {				
			}
			
			public boolean execute(Object param) throws Exception {
				System.out.println("Stump Ghost: <eyes off> command");
				
				Thread.sleep(100);
				
				System.out.println("Stump Ghost: <eyes off> command - completed");
				return false;
			}
		});
	}
	
	
	private void initTriggers() {
		triggers.put("motion", new Trigger("motion"));
	}
	
	
	private void initIndicators() {
		indicators.put(DeviceDriver.INDICATOR_WPS, new Indicator(DeviceDriver.INDICATOR_WPS) {
			protected void register() {
				if (gpioController == null) {
					return;
				}
				
				wpsLedOut = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_11, "WPS LED", PinState.LOW);
				wpsLedOut.setShutdownOptions(true, PinState.LOW);
			}
			
			protected void setState(final boolean on) {
				System.out.println("Stump Ghost: <WPS LED>: " + on);
				
				wpsLedOut.setState(on);
			}
			
			protected boolean getState() {
				return wpsLedOut.getState() == PinState.HIGH;
			}
		});
		indicators.put(DeviceDriver.INDICATOR_NETWORK, new Indicator(DeviceDriver.INDICATOR_NETWORK) {
			private boolean state;
			
			protected void register() {				
			}
			
			protected void setState(final boolean on) {
				System.out.println("Stump Ghost: <Network LED>: " + on);
				state = on;
			}

			protected boolean getState() {
				return state;
			}
		});
	}
	
	private void initButtons() {
		buttons.put(DeviceDriver.BUTTON_WPS, new Button(DeviceDriver.BUTTON_WPS) {
			private long buttonPressTimestamp = -1;
			
			public void register() {
				if (gpioController == null) {
					return;
				}
				
				wpsButtonIn = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_06, "WPS Button", PinPullResistance.PULL_DOWN);
				wpsButtonIn.setShutdownOptions(true, PinState.LOW);
				wpsButtonIn.addListener(new GpioPinListenerDigital() {
					public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
						System.out.println("WPS Button got an event. State = " + event.getState());

						if (buttonPressTimestamp == -1) {
							buttonPressTimestamp = System.currentTimeMillis();
						} else {
							long holdDuration = System.currentTimeMillis() - buttonPressTimestamp;
							if (holdDuration > 2000) {
								System.out.println("WPS Button was hold for more than 2 seconds - starting the sequence");
								notifyListeners();
							}
							
							buttonPressTimestamp = -1;
						}
					}
				});
			}
		});
	}

	
	
	
	public Command getCommand(final String commandName) {
		return commands.get(commandName);
	}

	public Trigger getTrigger(String name) {
		return triggers.get(name);
	}
	
	public Indicator getIndicator(String name) {
		return indicators.get(name);
	}
	
	public Button getButton(String name) {
		return buttons.get(name);
	}
}

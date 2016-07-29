package com.piztec.hween.controller;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Driver {
	public static class Control {
		private GpioController gpioController = GpioFactory.getInstance();
		
		private final String name;
		private final GpioPinDigitalOutput pin;
		
		Control(final String name, final Pin pinId) {
			this.name = name;
			this.pin = gpioController.provisionDigitalOutputPin(pinId, name, PinState.LOW);
			
			pin.setShutdownOptions(true, PinState.LOW);
		}
		
		public void turnOn(final int timeout) {
			pin.low();
			pin.pulse(timeout);
		}
		
		public void turnOn() {
			pin.high();
		}
		
		public void turnOff() {
			pin.low();
		}
	

		public String toString() {
			return name;
		}
	}
	
	public static Control CONTROL_LEFT_EYE = new Control("leftEye", RaspiPin.GPIO_01);
	public static Control CONTROL_RIGHT_EYE = new Control("rightEye", RaspiPin.GPIO_02);
	public static Control CONTROL_HEAD_UP = new Control("headUp", RaspiPin.GPIO_03);
	public static Control CONTROL_HEAD_DOWN = new Control("headDown", RaspiPin.GPIO_04);
	public static Control CONTROL_HEAD_TURN_RIGHT = new Control("headTurnRight", RaspiPin.GPIO_05);
	public static Control CONTROL_HEAD_TURN_LEFT = new Control("headTurnLeft", RaspiPin.GPIO_06);
	
	
	private static Driver instance = new Driver();
	
	public static Driver getInstance() {
		return instance;
	}
	
	private Driver() {
	}
	

	public void turnControlOn(final Control control) {
		control.turnOn();
	}

	public void turnControlOn(final Control control, final int timeout) {
		control.turnOn(timeout);
	}

	public void turnControlOff(final Control control) {
		control.turnOff();
	}
}

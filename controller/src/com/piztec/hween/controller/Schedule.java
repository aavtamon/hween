package com.piztec.hween.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.piztec.hween.controller.drivers.DeviceDriver;
import com.piztec.hween.controller.drivers.DeviceDriver.Command;
import com.piztec.hween.controller.drivers.DeviceDriver.Trigger;
import com.piztec.hween.controller.drivers.DeviceDriver.Trigger.TriggerListener;

public class Schedule {
	private static final String TRIGGER_IMMEDIATELY = "immediately";
	private static final String TRIGGER_DELAY = "delay";
	
	private final JSONObject cloudSchedule;
	private final DeviceDriver driver;
	private final Trigger trigger;
	private String triggerName;
	private Thread executionThread;
	
	
	public Schedule(final JSONObject cloudSchedule, final DeviceDriver driver) {
		this.cloudSchedule = cloudSchedule;
		this.driver = driver;

		try {
			triggerName = cloudSchedule.getString("trigger");
		} catch (Exception e) {
			e.printStackTrace();
			triggerName = TRIGGER_IMMEDIATELY;
		}
		
		trigger = driver.getTrigger(triggerName);
	}
	
	void execute() {
		if (executionThread != null && !executionThread.isInterrupted()) {
			return;
		}
		
		executionThread = new Thread() {
			private int programCounter = 0;
			private Object triggerLock = new Object();
			
			public void run() {
				if (trigger != null) {
					trigger.addTriggerListener(new TriggerListener() {
						public void onTriggerEvent() {
							synchronized (triggerLock) {
								triggerLock.notify();
							}
						}
					});
				}
				
				while (true) {
					if (trigger != null) {
						synchronized (triggerLock) {
							try {
								triggerLock.wait();
							} catch (InterruptedException e) {
							}
						}
					}
					
					JSONObject program = getNextProgram();
					executeProgram(program);
					
					if (isInterrupted()) {
						break;
					}

					if (trigger == null) {
						if (TRIGGER_IMMEDIATELY.equals(triggerName)) {
							// do nothing
						} else if (TRIGGER_DELAY.equals(triggerName)) {
							try {
								Thread.sleep(10000);
							} catch (InterruptedException e) {
							}
						}
					}
				}
			}
			
			private JSONObject getNextProgram() {
				try {
					JSONArray programs = cloudSchedule.getJSONArray("programs");
					
					JSONObject nextProgram = null;
					int initialProgramCounter = programCounter;
					while (true) {
						JSONObject program = programs.getJSONObject(programCounter);
						String frequency = program.getString("frequency");
						if (frequency.equals("never")) {
						} else {
							nextProgram = program;
						}
						
						programCounter++;
						if (nextProgram != null) {
							break;
						}
						
						if (programCounter == programs.length()) {
							programCounter = 0;
						}
						if (programCounter == initialProgramCounter) {
							break;
						}
					}
					
					return nextProgram;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
			private void executeProgram(JSONObject program) {
				System.out.println("Executing program: " + program);
				try {
					JSONArray cloudCommands = program.getJSONArray("commands");
					for (int i = 0; i < cloudCommands.length(); i++) {
						JSONObject cloudCommand = cloudCommands.getJSONObject(i);
						executeCommand(cloudCommand);
						
						if (isInterrupted()) {
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
					
			private void executeCommand(JSONObject cloudCommand) throws JSONException {
				String commandName = cloudCommand.getString("name");
				
				Command command = driver.getCommand(commandName);
				if (command != null) {
					command.execute(null);
				}
			}
 		};
 	
		executionThread.start();
	}
	
	void interrupt() {
		if (executionThread != null) {
			executionThread.interrupt();
		}
	}
	
}

package com.piztec.hween.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.piztec.hween.controller.drivers.DeviceDriver;
import com.piztec.hween.controller.drivers.DeviceDriver.Command;
import com.piztec.hween.controller.drivers.DeviceDriver.Trigger;
import com.piztec.hween.controller.drivers.DeviceDriver.Trigger.TriggerListener;
import com.piztec.hween.controller.media.AudioManager;

public class Schedule {
	private static final String TRIGGER_IMMEDIATELY = "immediately";
	private static final String TRIGGER_DELAY = "delay";
	
	private static final String COMMAND_PAUSE = "pause";
	private static final String COMMAND_TALK = "talk";
	
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
								break;
							}
						}
					}
					
					JSONObject program = getNextProgram();
					if (program == null) {
						//Nothing to execute in this schedule
						break;
					}
					
					try {
						executeProgram(program);
					} catch (InterruptedException e) {
						break;
					}
				  	
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
								break;
							}
						}
					}
				}
			}
			
			private JSONObject getNextProgram() {
				try {
					JSONArray programs = cloudSchedule.getJSONArray("programs");
					if (programs.length() == 0) {
						return null;
					}
					
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
						if (programCounter == programs.length()) {
							programCounter = 0;
						}

						if (nextProgram != null) {
							break;
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
			
			private void executeProgram(JSONObject program) throws InterruptedException {
				try {
					System.out.println("Executing program: " + program.getString("title"));
				} catch (JSONException e1) {
				}
				
				try {
					JSONArray cloudCommands = program.getJSONArray("commands");
					for (int i = 0; i < cloudCommands.length(); i++) {
						JSONObject cloudCommand = cloudCommands.getJSONObject(i);
						executeCommand(cloudCommand);
						
						if (isInterrupted()) {
							break;
						}
					}
					
					// We interrupt any playback left over from the previous program
					AudioManager.getInstance().stop();
				} catch (InterruptedException ie) {
					AudioManager.getInstance().stop();
					throw ie;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
					
			private void executeCommand(JSONObject cloudCommand) throws JSONException, Exception {
				String commandName = cloudCommand.getString("data");
				Object arg = null;
				try {
					arg = cloudCommand.get("arg");
				} catch (Exception e) {
				}
				
				System.out.println("Executing command " + commandName);
				
				// Special commands first
				if (COMMAND_PAUSE.equals(commandName)) {
					int delay = 3;
					if (arg != null) {
						try {
							delay = Integer.parseInt(arg.toString());
						} catch (Exception e) {
						}
					}
					Thread.sleep(delay * 1000);
				} else if (COMMAND_TALK.equals(commandName)) {
					int duration = AudioManager.getInstance().play(arg.toString());
					int delay = duration > 0 ? Math.min(duration, 1000) : 1000;
					Thread.sleep(delay);
				} else {
					Command command = driver.getCommand(commandName);
					if (command != null) {
						command.execute(arg);
					}
				}
			}
 		};
 	
		executionThread.start();
	}
	
	void interrupt() {
		if (executionThread != null) {
			executionThread.interrupt();

			try {
				executionThread.join();
			} catch (InterruptedException e) {
			}
		}
	}
}

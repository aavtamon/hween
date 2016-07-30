package com.piztec.hween.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.piztec.hween.controller.drivers.DeviceDriver.Command;

public class Schedule {
	private JSONObject cloudSchedule;
	private Thread executionThread;
	
	
	public Schedule(JSONObject cloudSchedule) {
		this.cloudSchedule = cloudSchedule;
	}
	
	void execute() {
		if (executionThread != null && !executionThread.isInterrupted()) {
			return;
		}
		
		executionThread = new Thread() {
			public void run() {
				try {
					JSONArray programs = cloudSchedule.getJSONArray("programs");
					
					for (int i = 0; i < programs.length(); i++) {
						JSONObject program = programs.getJSONObject(i);
						executeProgram(program);
						
						if (isInterrupted()) {
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			private void executeProgram(JSONObject program) throws JSONException {
				JSONArray cloudCommands = program.getJSONArray("commands");
				for (int i = 0; i < cloudCommands.length(); i++) {
					try {
						JSONObject cloudCommand = cloudCommands.getJSONObject(i);
						executeCommand(cloudCommand);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if (isInterrupted()) {
						break;
					}
				}
			}
					
			private void executeCommand(JSONObject cloudCommand) throws JSONException {
				String commandName = cloudCommand.getString("name");
				
				Command command = DriverManager.getDriver().getCommand(commandName);
				if (command != null) {
					command.execute(null);
				}
			}
			
 		};
 		
		executionThread.start();
	}
	
	void interrupt() {
		executionThread.interrupt();
	}
	
}

package com.piztec.hween.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

public class ControlServer {
	private static final String OPERATION_CONNECT_TO_BACKEND = "connect_to_backend";
	private static final String OPERATION_MANUAL_COMMAND = "manual_command";
	
	static final int SERVER_PORT = 8888;
	
	private static ControlServer instance;
	
	private Thread socketThread;
	
	
	private interface ControllerAction {
		public void perform(String operation);
	}
	
	
	public static ControlServer getInstance() {
		if (instance == null) {
			instance = new ControlServer();
		};
		
		return instance;
	}
	
	private ControlServer() {
	}
	
	
	public void start() {
		socketThread = new Thread() {
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
					
					while (!isInterrupted()) {
						handleSocketConnection(serverSocket.accept(), new ControllerAction() {
							public void perform(String operation) {
								handleOperation(operation);
							}
						});
					}
					
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		socketThread.start();
	}
	
	public void stop() {
		socketThread.interrupt();
	}
	
	
	private void handleSocketConnection(final Socket clientSocket, final ControllerAction action) throws IOException {
	    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	    
        String line = in.readLine();
        if (line.startsWith("OPTIONS ")) {
        	generateResponse(out, null);
        } else if (line.startsWith("PUT ")) {
        	int contentLength = 0;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
            	if (line.startsWith("Content-Length: ")) {
            		contentLength = Integer.valueOf(line.substring("Content-Length: ".length()));
            	}
            }
        	
            if (contentLength > 0) {
            	char[] content = new char[contentLength];
            	in.read(content, 0, contentLength);
            	
            	action.perform(String.valueOf(content));
            }
            
            generateResponse(out, null);
        }
        
        in.close();
        out.close();
	    
        clientSocket.close();
	}
	
	private void generateResponse(final PrintWriter output, final String content) {
		output.println("HTTP/1.1 200 OK");
		output.println("Server: Device Controller");
		output.println("Access-Control-Allow-Headers: origin, content-type, accept, authorization, token, location");
		output.println("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, HEAD");
		output.println("Access-Control-Allow-Origin: *");
		if (content != null) {
			output.println("Content-Length: " + content.getBytes().length);
		}
		
	    SimpleDateFormat sdfDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	    sdfDate.setTimeZone(TimeZone.getDefault());
		output.println("Date: " + sdfDate.format(new Date()));

		if (content != null) {
			output.println();
			output.println(content);
		}
	}
	
	
	private void handleOperation(final String operationText) {
		try {
			JSONObject message = new JSONObject(operationText);
			String operation = message.getString("operation");
			if (OPERATION_CONNECT_TO_BACKEND.equals(operation)) {
				CloudAccessor.getInstance().enableOftenReporting();
			} else if (OPERATION_MANUAL_COMMAND.equals(operation)) {
				String command = message.getString("command");
				
				String arg = null;
				try {
					arg = message.getString("arg");
				} catch (JSONException je) {					
				}
				
				DeviceManager.getInstance().executeCommand(command, arg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

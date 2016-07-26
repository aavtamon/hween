package com.piztec.hween.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ControlServer {
	static final int SERVER_PORT = 8888;
	
	private static ControlServer instance;
	
	private Thread socketThread;
	
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
						System.err.println("Waiting for the server to be accepted");
						handleSocketConnection(serverSocket.accept());
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
	
	
	private void handleSocketConnection(Socket clientSocket) throws IOException {
		System.err.println("Started processing");
	    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));				
		
        StringBuilder responseText = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            responseText.append(line);
        }
        in.close();
        
        System.out.println("Server: " + responseText);
	    
        clientSocket.close();
	}
}

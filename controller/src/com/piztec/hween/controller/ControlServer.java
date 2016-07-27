package com.piztec.hween.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
						String message = handleSocketConnection(serverSocket.accept());
						if (message != null) {
							handleMessage(message);
						}
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
	
	
	private String handleSocketConnection(Socket clientSocket) throws IOException {
	    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	    
	    String result = null;
	    
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
            	
            	result = String.valueOf(content);
            }
            
            generateResponse(out, null);
        }
        
        in.close();
        out.close();
	    
        clientSocket.close();
        
        return result;
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
		output.println("Date: Wed, 27 Jul 2016 02:31:24 GMT");
		if (content != null) {
			output.println();
			output.println(content);
		}
	}
	
	
	private void handleMessage(final String message) {
		System.out.println("Handling message: " + message);
	}
}
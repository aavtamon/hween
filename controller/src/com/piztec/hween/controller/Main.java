package com.piztec.hween.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.piztec.hween.controller.network.ConnectionManager;

public class Main {
	public static void main(String[] args) throws Exception {
		String url = System.getProperty("server_url");
		if (url == null) {
			System.err.println("URL is not specified");
			return;
		}
		
		CloudAccessor ca = new CloudAccessor(url);
		ca.start();
		
		ControlServer cs = new ControlServer(ca);
		cs.start();
	}
}

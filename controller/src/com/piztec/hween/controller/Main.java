package com.piztec.hween.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.piztec.hween.controller.media.NetworkManager;

public class Main {
	public static void main(String[] args) throws Exception {
		String url = System.getProperty("server_url");
		if (url == null) {
			System.err.println("URL is not specified");
			return;
		}
		ControllerContext.setServerUrl(url);
		
		CloudAccessor ca = new CloudAccessor();
		ca.start();
		
		ControlServer cs = new ControlServer(ca);
		cs.start();
	}
}

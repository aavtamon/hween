package com.piztec.hween.controller;

public class ControllerContext {
	private static String serverUrl;
	
	static void setServerUrl(final String url) {
		serverUrl = url;
	}
	
	public static String getServerUrl() {
		return serverUrl;
	}
}

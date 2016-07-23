package com.piztec.hween.application;

public class ApplicationUtils {
	public static boolean isAuthenticated(final String authHeader) {
		if (authHeader == null) {
			return false;
		}
		
		String[] authTokens = authHeader.split(":");
		if (authTokens.length != 2) {
			return false;
		}
		
		if (authTokens[0] == null || authTokens[1] == null) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isAuthenticated(final int userId, final String authHeader) {
		if (!isAuthenticated(authHeader)) {
			return false;
		}
		
		return true;
	}
}

package com.piztec.hween.admin;


public class AdminUtils {
  private static final String ADMIN_LOGIN = "root";
  private static final String ADMIN_PASSWORD = "password";
  
	public static boolean isAuthorized(final String authHeader) {
		if (authHeader == null) {
			return false;
		}
		
		String[] loginPassword = authHeader.split("=");
		if (loginPassword.length != 2) {
		  return false;
		}
				
		return ADMIN_LOGIN.equals(loginPassword[0]) && ADMIN_PASSWORD.equals(loginPassword[1]);
	}
}

package com.piztec.hween.persistance;

public class PersistanceUtils {
	private static long lastGeneratedId = 0;	
	private static long lastGeneratedRevision = 0;
	
	
	public static int generateUniqueId() {
//		long idCandidate = System.currentTimeMillis();
//		return (int)(idCandidate % Integer.MAX_VALUE);
		
		long idCandidate = lastGeneratedId++;
		return (int)(idCandidate % Integer.MAX_VALUE);
	}
	
	public static long generateRevision() {
		return lastGeneratedRevision++;
	}
}

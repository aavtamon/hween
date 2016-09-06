package com.piztec.hween.persistance;

public class PersistanceUtils {
	public static int generateUniqueId() {
		long idCandidate = System.currentTimeMillis();
		return (int)(idCandidate % Integer.MAX_VALUE);
	}
}

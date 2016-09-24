package com.piztec.hween.persistance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
	
	
	public static String readFile(final String filePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    return sb.toString();
		} catch (Exception e) {
		} finally {
			if (br != null) {
			    try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		
		return null;
	}
}

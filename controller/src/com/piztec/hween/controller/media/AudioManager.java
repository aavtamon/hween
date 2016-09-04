package com.piztec.hween.controller.media;

import java.io.ByteArrayInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.xml.bind.DatatypeConverter;

public class AudioManager {
	private static AudioManager instance;
	
	public static AudioManager getInstance() {
		if (instance == null) {
			instance = new AudioManager();
		}
		
		return instance;
	}
	
	
	private Clip currentClip;
	
	
	private AudioManager() {		
	}
	
	public int play(final String audioDesriptor) {
		if (audioDesriptor == null) {
			return -1;
		}
		
		int mimeSeparator = audioDesriptor.indexOf(";");
		if (mimeSeparator == -1) {
			return -1;
		}
		String mimeType = audioDesriptor.substring(5,  mimeSeparator);
		if (!mimeType.startsWith("audio")) {
			return -1;
		}
		
		String audioData = audioDesriptor.substring(mimeSeparator + 8);
		byte[] audioBytes = DatatypeConverter.parseBase64Binary(audioData);
		
		stop(); // attempt to stop if some other playback is still in progress

		try {
	    	AudioInputStream audioIn = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioBytes));
	        currentClip = AudioSystem.getClip();
	        currentClip.open(audioIn);
	        currentClip.start();
		} catch (Exception e) {
			System.err.println("Failed to play a clip: " + e);
			return -1;
		}
		
        return (int)(currentClip.getMicrosecondLength() / 1000);		
	}
	
	public void stop() {
		if (currentClip != null) {
			currentClip.stop();
			currentClip.close();
			currentClip = null;
		}
	}
}

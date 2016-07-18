package com.piztec.hween.persistance;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class StorageManager {
	private static final StorageManager instance = new StorageManager();

	private EntityManagerFactory f;

	public static StorageManager getInstance() {
		return instance;
	}

	public EntityManager getEntityManager() {
		return f.createEntityManager();
	}

	private StorageManager() {
		f = Persistence.createEntityManagerFactory("HweenToy");

		init();
	}

	private void init() {
		System.out.println("BEFORE Database recreated");
		
		//init device settings;
		EntityManager em = getEntityManager();
		em.clear();

		DeviceSettings ds = new DeviceSettings();
		ds.setDeviceType(DeviceSettings.STUMP_GHOST_TYPE);
		
		List<DisplayableElement> categories = new ArrayList<DisplayableElement>();
		categories.add(new DisplayableElement("fun", "Fun"));
		categories.add(new DisplayableElement("scary", "Scary"));
//		ds.setCategories(categories);

		List<DisplayableElement> commands = new ArrayList<DisplayableElement>();
		commands.add(new DisplayableElement("reset", "Reset", "Sets toy to the initial position"));
		commands.add(new DisplayableElement("move_up", "Move Up", "Move toy up one inch"));
		commands.add(new DisplayableElement("move_down", "Move Down", "Move toy down one inch"));
		commands.add(new DisplayableElement("turn_left", "Turn Left", "Turn left a bit"));
		commands.add(new DisplayableElement("turn_right", "Turn Right", "Turn right a bit"));
		commands.add(new DisplayableElement("eyes_on", "Turn Eyes On", "Turn eyes on"));
		commands.add(new DisplayableElement("eyes_off", "Turn Eyes Off", "Turn eyes off"));
		commands.add(new DisplayableElement("talk", "Speak", "Say something"));
		commands.add(new DisplayableElement("pause", "Do nothing", "Do nothing for a bit"));
		commands.add(new DisplayableElement("pause", "Do nothing"));
//		ds.setSupportedCommands(commands);

		List<DisplayableElement> triggers = new ArrayList<DisplayableElement>();
		triggers.add(new DisplayableElement("immediately", "Previous"));
		triggers.add(new DisplayableElement("delay", "Delay"));
		triggers.add(new DisplayableElement("motion", "Motion"));
//		ds.setSupportedProgramTriggers(triggers);
		
		
		
		em.getTransaction().begin();
		em.merge(ds);
		em.getTransaction().commit();

		System.out.println("Database recreated");
	}
}

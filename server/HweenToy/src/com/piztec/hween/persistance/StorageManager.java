package com.piztec.hween.persistance;

import java.sql.Connection;
import java.sql.DriverManager;

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
	}
}

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
//		try {
//			Class.forName("org.sqlite.JDBC");
//			Connection connection = DriverManager.getConnection("jdbc:sqlite:/Users/aavtamonov/project/other/hween/server/db/hween.db");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		f = Persistence.createEntityManagerFactory("HweenToy");
		System.out.println("here");
	}
}

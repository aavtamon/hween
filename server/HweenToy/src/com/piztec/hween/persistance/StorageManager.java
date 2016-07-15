package com.piztec.hween.persistance;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.persistence.Persistence;

import org.springframework.orm.jpa.EntityManagerFactoryAccessor;

public class StorageManager {
	private static final StorageManager instance = new StorageManager();
	
	public static StorageManager getInstance() {
		return instance;
	}
	
	private StorageManager() {
//		try {
//			Class.forName("org.sqlite.JDBC");
//			Connection connection = DriverManager.getConnection("jdbc:sqlite:/Users/aavtamonov/project/other/hween/server/db/hween.db");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		Persistence.createEntityManagerFactory("HweenToy");
		System.out.println("here");
	}
}

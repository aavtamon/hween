package com.piztec.hween.persistance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Test {
	  @Id @GeneratedValue
	  private int id;
	  private String text;
	  public Test() {
	  }
	  public int getId() {
	      return id;
	  }
	  public void setId(int id) {
	      this.id = id;
	  }
	  public String getText() {
	      return text;
	  }
	  public void setText(String text) {
	      this.text = text;
	  }
}

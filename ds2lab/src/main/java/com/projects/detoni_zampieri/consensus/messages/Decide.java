package com.projects.detoni_zampieri.consensus.messages;

import java.io.Serializable;

public class Decide implements Serializable {

	public Decide(int value) {
		this.value = value;
	}
	
	public int value;
	
}

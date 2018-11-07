package com.projects.detoni_zampieri.consensus.messages;

import java.io.Serializable;

public class Phase2 implements Serializable {

	public Phase2(int round,int value) {
		this.round = round;
		this.value = value;
	}
	
	public int round;
	public int value;
}

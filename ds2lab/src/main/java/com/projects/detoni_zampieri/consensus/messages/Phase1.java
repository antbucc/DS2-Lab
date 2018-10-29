package com.projects.detoni_zampieri.consensus.messages;

import java.io.Serializable;

public class Phase1 implements Serializable {

	public int round,est,pid;
	
	public Phase1(int round,int est,int pid) {
		this.round = round;
		this.est = est;
		this.pid = pid;
	}
}

package com.projects.detoni_zampieri.lab2.message;

public class PushMessage extends Message {
	
	private EpidemicValue value;
	
	public PushMessage(EpidemicValue v) {
		this.value = v;
	}
}

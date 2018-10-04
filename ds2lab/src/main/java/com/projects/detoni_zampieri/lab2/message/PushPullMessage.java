package com.projects.detoni_zampieri.lab2.message;


public class PushPullMessage extends Message{
	public EpidemicValue value;
	
	public PushPullMessage(EpidemicValue value) {
		this.value = value;
	}
}
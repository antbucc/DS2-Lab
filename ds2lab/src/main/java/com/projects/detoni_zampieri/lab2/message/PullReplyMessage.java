package com.projects.detoni_zampieri.lab2.message;

public class PullReplyMessage extends Message {
	private EpidemicValue value;
	
	public PullReplyMessage(EpidemicValue value) {
		this.value = value;
	}
}
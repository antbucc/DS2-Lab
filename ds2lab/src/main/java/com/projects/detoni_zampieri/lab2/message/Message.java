package com.projects.detoni_zampieri.lab2.message;

import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable {

    public Message()
    {
    }
    
}


class PushMessage implements Serializable{
	private EpidemicValue value;
	
	public PushMessage(EpidemicValue v) {
		this.value = v;
	}
}

class PullRequestMessage implements Serializable{
	private Timestamp timestamp;
	
	public PullRequestMessage(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}

class PullReplyMessage implements Serializable{
	private EpidemicValue value;
	
	public PullReplyMessage(EpidemicValue value) {
		this.value = value;
	}
}

class PushPullMessage implements Serializable{
	private EpidemicValue value;
	
	public PushPullMessage(EpidemicValue value) {
		this.value = value;
	}
}

class EpidemicValue {

	private Timestamp timestamp;
	private int value;
	
	public EpidemicValue(Timestamp timestamp,int value) {
		this.timestamp = timestamp;
		this.value = value;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
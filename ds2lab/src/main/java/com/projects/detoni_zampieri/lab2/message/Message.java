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


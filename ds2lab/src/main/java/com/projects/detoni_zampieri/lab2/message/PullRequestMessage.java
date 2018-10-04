package com.projects.detoni_zampieri.lab2.message;

import java.io.Serializable;
import java.sql.Timestamp;

public class PullRequestMessage extends Message {
	private Timestamp timestamp;
	
	public PullRequestMessage(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}

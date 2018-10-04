package com.projects.detoni_zampieri.lab2.message;

import java.sql.Timestamp;

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

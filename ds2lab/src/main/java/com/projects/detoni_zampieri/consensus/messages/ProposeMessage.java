package com.projects.detoni_zampieri.consensus.messages;

import java.io.Serializable;

public class ProposeMessage implements Serializable {

	public int value;
	
	public ProposeMessage(int v) {
		this.value = v;
	}
}

package com.projects.detoni_zampieri.chord.messages;

import java.io.Serializable;
import java.util.List;

import akka.actor.ActorRef;

public class ListMessage implements Serializable {

	public List<ActorRef> peers;
	
	public ListMessage(List<ActorRef> peers) {
		this.peers = peers;
	}
}


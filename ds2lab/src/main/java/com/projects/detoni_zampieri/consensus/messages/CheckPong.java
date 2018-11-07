package com.projects.detoni_zampieri.consensus.messages;

import java.io.Serializable;

import akka.actor.ActorRef;

public class CheckPong implements Serializable {

	public ActorRef node;
	
	public CheckPong(ActorRef node) {
		this.node = node;
	}
	
}

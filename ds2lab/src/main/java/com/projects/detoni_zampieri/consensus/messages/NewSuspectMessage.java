package com.projects.detoni_zampieri.consensus.messages;

import java.io.Serializable;

import akka.actor.ActorRef;

public class NewSuspectMessage implements Serializable {
	public ActorRef suspect;
	
	public NewSuspectMessage(ActorRef suspect) {
		this.suspect = suspect;
	}
}

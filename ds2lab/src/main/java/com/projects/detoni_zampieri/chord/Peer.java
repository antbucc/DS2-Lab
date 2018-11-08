package com.projects.detoni_zampieri.chord;

import com.projects.detoni_zampieri.chord.messages.*;

import akka.actor.UntypedActor;

public class Peer extends UntypedActor{

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof ListMessage) {
			// TODO
		} else {
			unhandled(msg);
		}
	}

}

package com.projects.detoni_zampieri.consensus;

import java.util.List;
import java.util.ArrayList;

import com.projects.detoni_zampieri.consensus.messages.*;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Peer extends UntypedActor {
	
	public Peer() {
		this.globalClock = GlobalClock.getClock();
		
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof ListMessage) {
			onListMessage((ListMessage)msg);
		} else if(msg instanceof PingMessage){
			getSender().tell(new PongMessage(), getSelf());
		} else {
			unhandled(msg);
		}

	}
	
	public void onListMessage(ListMessage msg) {
		this.peers = new ArrayList<>(((ListMessage) msg).peers);
	}
	

	public List<ActorRef> peers;
	public GlobalClock globalClock;
}

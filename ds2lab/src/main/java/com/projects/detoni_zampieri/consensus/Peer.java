package com.projects.detoni_zampieri.consensus;

import java.util.List;
import java.util.ArrayList;

import com.projects.detoni_zampieri.consensus.messages.*;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Peer extends UntypedActor {
	
	public Peer(int id) {
		this.globalClock = GlobalClock.getClock();
		this.proposed = this.id = id;
		this.decided = this.stop = false;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof ListMessage) {
			onListMessage((ListMessage)msg);
		} else if(msg instanceof PingMessage){
			getSender().tell(new PongMessage(), getSelf());
		} else if(msg instanceof ProposeMessage){
			onProposeMessage((ProposeMessage)msg);
		} else if(msg instanceof Phase1){
			onPhase1((Phase1)msg);
		} else {
			unhandled(msg);
		}

	}
	
	public void onListMessage(ListMessage msg) {
		this.peers = new ArrayList<>(((ListMessage) msg).peers);
		this.n = this.peers.size();
	}
	
	public void onProposeMessage(ProposeMessage msg) {
		this.est = msg.value;
		this.round = 0;
		enterPhaseOne();
	}
	
	public void enterPhaseOne() {
		this.c = this.round % n;
		this.round++;
		if(this.c == this.id) {
			Phase1 p1 = new Phase1(this.round, this.est, this.id);
			broadcast(p1);
			getSelf().tell(p1, getSelf());
		}
	}
	
	public void onPhase1(Phase1 msg) {
		//TODO account for mismatching epochs
		this.aux = msg.est;
		enterPhase2();
	}
	
	public void enterPhase2() {
		// TODO complete
	}
	
	public void broadcast(Object message) {
		ActorRef self = getSelf();
		for(ActorRef p:this.peers) {
			if(!p.equals(self)) {
				p.tell(message, self);
			}
		}
	}
	
	public static Props props(int id) {
		return Props.create(Peer.class,()->new Peer(id));
	}

	public List<ActorRef> peers;
	public GlobalClock globalClock;
	public int round,proposed,est;
	public boolean stop,decided;
	public int id,n; 	// n is the number of peers in the system at the moment of a consensus round
	public int c,aux; 		// c is the current leader in the consensus protocol
}

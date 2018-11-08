package com.projects.detoni_zampieri.consensus;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;

import com.projects.detoni_zampieri.consensus.messages.*;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import java.util.HashSet;

public class Peer extends UntypedActor {
	
	public Peer(int id) {
		this.globalClock = GlobalClock.getClock();
		this.proposed = this.id = id;
		this.decided = this.stop = false;
		this.proc = new HashSet<>();
		this.rec = new HashSet<>();
		this.suspected = new HashSet<>();
		this.hasCrashed = false;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if(!this.hasCrashed) {
			if(msg instanceof ListMessage) {
				ListMessage m = (ListMessage)msg;
				onListMessage(m);
				this.fd = getContext().system().actorOf(EventuallyStrongFD.props(new ArrayList<>(m.peers), getSelf()),"fd-"+this.id);
			} else if(msg instanceof PingMessage){
				getSender().tell(new PongMessage(), getSelf());
			} else if(msg instanceof ProposeMessage){
				onProposeMessage((ProposeMessage)msg);
			} else if(msg instanceof Phase1){
				onPhase1((Phase1)msg);
			} else if(msg instanceof NewSuspectMessage){
				NewSuspectMessage m=(NewSuspectMessage)msg;
				this.suspected.add(m.suspect);
				if(this.waitingPhase1Message && this.actorToID.get(m.suspect)==this.c) {
					this.aux = Peer.questionMark;
					this.waitingPhase1Message = false;
					enterPhase2();
				}
			} else if(msg instanceof Phase2){
				onPhase2((Phase2)msg);
			} else if(msg instanceof Decide){
				onDecide((Decide)msg);
			} else if(msg instanceof StartConsensus){
				broadcast(new ProposeMessage(this.id),true);
			} else if(msg instanceof Crash){
				this.hasCrashed = true;
			} else {
				unhandled(msg);
			}
		}
	}
	
	public void onListMessage(ListMessage msg) {
		this.peers = new ArrayList<>(((ListMessage) msg).peers);
		this.n = this.peers.size();
		this.actorToID = new HashMap<ActorRef, Integer>(msg.actorToID);
	}
	
	public void onProposeMessage(ProposeMessage msg) {
		this.est = msg.value;
		this.round = 0;
		this.decided = this.stop = false;
		enterPhase1();
	}
	
	public void enterPhase1() {
		if(!this.stop) {
			this.c = this.round % n;
			this.round++;
			if(this.c == this.id) {
				Phase1 p1 = new Phase1(this.round, this.est, this.id);
				broadcast(p1,true);
			}
			this.waitingPhase1Message = true;
			if(isSuspected(suspected, c)) {
				this.aux = Peer.questionMark;
				this.waitingPhase1Message = false;
				enterPhase2();
			}
		}
	}
	
	public boolean isSuspected(Set<ActorRef> s,int id) {
		for(ActorRef a:s) {
			if(this.actorToID.get(a)==id) {
				return true;
			}
		}
		return false;
	}
	
	public void onPhase1(Phase1 msg) {
		//TODO account for mismatching epochs
		this.aux = msg.est;
		this.waitingPhase1Message = false;
		enterPhase2();
	}
	
	public void enterPhase2() {
		broadcast(new Phase2(this.round,this.aux),true);
		this.proc.clear();
		this.rec.clear();
		this.waitingPhase2Message =true;
	}
	
	public int getNonQuestionMarkValue(Set<Integer> s) {
		for(Integer v:s){
			if(v!=Peer.questionMark) {
				return v;
			}
		}
		//should never be returned
		return Peer.questionMark;
	}
	
	public void onPhase2(Phase2 msg) {
		if(this.waitingPhase2Message) {
			this.proc.add(getSender());
			this.rec.add(msg.value);
			if(this.proc.size()>this.n/2){
				this.waitingPhase2Message = false;
				int chosen = getNonQuestionMarkValue(this.rec);
				if(this.rec.size()==2) {	//received both ? and a value
					this.est = getNonQuestionMarkValue(this.rec);
					assert(this.est!=Peer.questionMark);
					enterPhase1();
				} else if(chosen != Peer.questionMark) {
					this.est = chosen;
					broadcast(new Decide(this.est),true);
					this.stop = true;
				} else {
					enterPhase1();
				}
			}
		}
	}
	
	public void onDecide(Decide msg) {
		if(!this.decided) {
			broadcast(new Decide(msg.value));
			this.decided=true;
			this.stop = true;
			this.decidedValue = msg.value;
			System.out.println("Node "+this.id+", round "+(this.round-1)+", decided "+this.decidedValue);
		}
	}
	
	public void broadcast(Object message) {
		ActorRef self = getSelf();
		for(ActorRef p:this.peers) {
			if(!p.equals(self)) {
				p.tell(message, self);
			}
		}
	}
	
	public void broadcast(Object message,boolean sendToSelf) {
		broadcast(message);
		if(sendToSelf) {
			getSelf().tell(message, getSelf());
		}
	}
	
	
	public static Props props(int id) {
		return Props.create(Peer.class,()->new Peer(id));
	}

	public static int questionMark = -1;
	public Map<ActorRef,Integer> actorToID;
	public List<ActorRef> peers;
	public GlobalClock globalClock;
	public ActorRef fd;
	public int round,proposed,est;
	public boolean stop,decided;
	public int id,n; 	// n is the number of peers in the system at the moment of a consensus round
	public int c,aux; 		// c is the current leader in the consensus protocol
	public boolean waitingPhase1Message;
	public Set<Integer> rec;
	public Set<ActorRef> proc;
	public boolean waitingPhase2Message;
	public int decidedValue;
	public Set<ActorRef> suspected;
	public boolean hasCrashed;
}

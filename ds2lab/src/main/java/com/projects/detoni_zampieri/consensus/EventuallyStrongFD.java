package com.projects.detoni_zampieri.consensus;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.projects.detoni_zampieri.consensus.messages.*;

public class EventuallyStrongFD extends UntypedActor{

	
	public EventuallyStrongFD(List<ActorRef> peers,ActorRef owner){
		this.peers = new ArrayList<>(peers);
		this.globalClock = GlobalClock.getClock();
		this.suspectedActors = new HashSet<>();
		this.maxTickDifference = 75;
		this.timeoutPing = 8000;
		this.owner = owner;
		this.lastPingSent= new HashMap<ActorRef, Integer>();
		this.lastPongReceived = new HashMap<>();
		scheduleTimeout(new FDTimeoutPingMessage(), this.timeoutPing);
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof PongMessage) {
			int tick = globalClock.currentTick();
			ActorRef sender = getSender();
			this.lastPongReceived.put(sender, true);
		} else if(msg instanceof FDTimeoutPingMessage){
			broadcastPing();
			scheduleTimeout(new FDTimeoutPingMessage(), this.maxTickDifference);			
		} else if(msg instanceof CheckPong){
			CheckPong m = (CheckPong) msg;
			if(!this.lastPongReceived.get(m.node) && !this.suspectedActors.contains(m.node)) {
				setSuspected(m.node);
			}
		} else {
			unhandled(msg);
		}
	}
	
	public void setSuspected(ActorRef actor) {
		suspectedActors.add(actor);
		owner.tell(new NewSuspectMessage(actor), getSelf());
		System.out.println("suspected "+actor);
	}
	
	public void broadcastPing() {
		int tick = this.globalClock.currentTick();
		for(ActorRef node:peers) {
			if(!this.suspectedActors.contains(node)) {
				node.tell(new PingMessage(), getSelf());
				this.lastPingSent.put(node, tick);
				this.lastPongReceived.put(node, false);
				scheduleTimeout(new CheckPong(node), (int)(this.maxTickDifference*globalClock.delta));
			}
		}
	}
	
	public void scheduleTimeout(Object timeoutMessage,int milliseconds) {
    	getContext().system().scheduler().scheduleOnce(
                new FiniteDuration(milliseconds, TimeUnit.MILLISECONDS),
                getSelf(),
                timeoutMessage,
                getContext().system().dispatcher(),
                getSelf()
        );
    }
	

	
	public static Props props(List<ActorRef> peers,ActorRef owner) {
		return Props.create(EventuallyStrongFD.class,()->new EventuallyStrongFD(peers, owner));
	}
	
	private ActorRef owner;
	private int timeoutPing;
	private int maxTickDifference;
	private Set<ActorRef> suspectedActors;
	private ArrayList<ActorRef> peers;
	private GlobalClock globalClock;
	private Map<ActorRef, Integer> lastPingSent;
	private Map<ActorRef, Boolean> lastPongReceived;
}

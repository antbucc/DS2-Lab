package com.projects.detoni_zampieri.consensus;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.HashSet;

import com.projects.detoni_zampieri.consensus.messages.*;

public class EventuallyStrongFD extends UntypedActor{

	
	public EventuallyStrongFD(List<ActorRef> peers){
		this.peers = new ArrayList<>(peers);
		this.globalClock = GlobalClock.getClock();
		this.suspectedActors = new HashSet<>();
		this.maxTickDifference = 5;
		this.timeoutPing = 300;
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof ListMessage) {
			this.peers = new ArrayList<>(((ListMessage)msg).peers);	
			scheduleTimeout(new FDTimeoutPingMessage(), this.timeoutPing);
		} else if(msg instanceof PongMessage) {
			int tick = globalClock.currentTick();
			ActorRef sender = getSender();
			if(lastPingSent.get(sender)-tick > this.maxTickDifference) {
				suspectedActors.add(sender);
			}
		} else if(msg instanceof FDTimeoutPingMessage){
			broadcastPing();
			scheduleTimeout(new FDTimeoutPingMessage(), this.maxTickDifference);			
		} else {
			unhandled(msg);
		}
	}
	
	public void broadcastPing() {
		int tick = this.globalClock.currentTick();
		for(ActorRef node:peers) {
			node.tell(new PingMessage(), getSelf());
			this.lastPingSent.put(node, tick);
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
	
	
	public Set<ActorRef> getSuspectedActors(){
		return this.suspectedActors;
	}
	
	private int timeoutPing;
	private int maxTickDifference;
	private Set<ActorRef> suspectedActors;
	private ArrayList<ActorRef> peers;
	private GlobalClock globalClock;
	private Map<ActorRef, Integer> lastPingSent;
}

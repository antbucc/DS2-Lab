/*
    Alberto Ercolani - 194431
    Lara Ceschi - 194427
*/


package com.projects.CeschiErcolani.ReliableMulticast;

import java.util.ArrayList;
import java.util.List;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class ApplicationMain {
	
	public static void main(String args[]) {
		int N = 4;		
		ActorSystem asSystem = ActorSystem.create("RBSystem");
	
		List<ActorRef> lGroup = new ArrayList<ActorRef>();
		for(int i = 1;i<=N;i++) {
			lGroup.add(asSystem.actorOf(ReliableBroadcast.props().withDispatcher("akka.actor.my-pinned-dispatcher"),
					"RB" + String.valueOf(i)));
		}
		
		for (ActorRef arActor : lGroup) { arActor.tell(new ReliableBroadcast.HelloMsg(lGroup), null); }
		
		
	}
}

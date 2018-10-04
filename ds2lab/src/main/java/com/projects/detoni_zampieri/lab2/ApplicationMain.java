package com.projects.detoni_zampieri.lab2;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.example.ReliableBroadcast;

import java.util.ArrayList;
import java.util.List;

public class ApplicationMain {

    public static void main(String[] args) {
    	
    	ActorSystem system = ActorSystem.create("MyActorSystem");
        
        int N = 4;
        List<ActorRef> ps = new ArrayList<ActorRef>();
        for (int i = 1; i <= N; i++) {
        	ps.add(system.actorOf(ReliableBroadcast.props().withDispatcher("akka.actor.my-pinned-dispatcher"), "RB" + String.valueOf(i)));
        }
        for (ActorRef p : ps) {
        	p.tell(new ReliableBroadcast.StartMessage(ps), null);
        }
        
        ps.get(0).tell(new ReliableBroadcast.BroadcastMessage("a"), null);
    }

} 
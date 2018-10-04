package com.example;


import java.util.ArrayList;
import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class ApplicationMain {

    public static void main(String[] args) throws InterruptedException {
    	
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
      /*  try {
        	Thread.sleep(200);
        } catch (Exception e) {
        	e.printStackTrace();
        }
       
        /*ps.get(0).tell(new ReliableBroadcast.BroadcastMessage("b"), null);
        try {
        	Thread.sleep(200);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        ps.get(2).tell(new ReliableBroadcast.BroadcastMessage("x"), null);
        try {
        	Thread.sleep(100);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        ps.get(0).tell(new ReliableBroadcast.BroadcastMessage("z"), null);
        */
        
        //ActorRef pingActor = system.actorOf(PingActor.props(), "pingActor");
        //pingActor.tell(new PingActor.Initialize(), null);
        // This example app will ping pong 3 times and thereafter terminate the ActorSystem -
        // see counter logic in PingActor
        system.whenTerminated().wait();
    }

} 
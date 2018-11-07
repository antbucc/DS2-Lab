package com.projects.detoni_zampieri.consensus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.projects.detoni_zampieri.consensus.Peer;
import com.projects.detoni_zampieri.consensus.messages.Crash;
import com.projects.detoni_zampieri.consensus.messages.ListMessage;
import com.projects.detoni_zampieri.consensus.messages.StartConsensus;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class ApplicationMain {

	public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("consensus");
        int numActors = 7;
        List<ActorRef> nodes = new ArrayList<ActorRef>();

        HashMap<ActorRef, Integer> atoi=new HashMap<>();
        // Create the set of actors
        for (int i = 0; i < numActors; i++) {
            ActorRef a = system.actorOf(Peer.props(i)
                    .withDispatcher("akka.actor.my-pinned-dispatcher"), "node-" + i);
            nodes.add(a);
            atoi.put(a, i);
        }

        // Send the list of all actors.
        for (int i = 0; i < numActors; i++) {
            nodes.get(i).tell(new ListMessage(nodes,new HashMap<>(atoi)), ActorRef.noSender());
        }
        
        try {
        	Thread.sleep(1000);
        } catch (Exception e) {
			e.printStackTrace();
		}

        nodes.get(0).tell(new Crash(), ActorRef.noSender());
        nodes.get(1).tell(new Crash(), ActorRef.noSender());
        nodes.get(2).tell(new Crash(), ActorRef.noSender());
        nodes.get(4).tell(new StartConsensus(), ActorRef.noSender());
        

        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
            
            System.out.println(">>> Stopped <<<");
        }
        catch (IOException ioe) {}
        system.terminate();

    }
}

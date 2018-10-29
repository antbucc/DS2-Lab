package com.projects.detoni_zampieri.consensus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.projects.detoni_zampieri.consensus.Peer;
import com.projects.detoni_zampieri.consensus.messages.ListMessage;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class ApplicationMain {

	public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("consensus");
        int numActors = 10;
        List<ActorRef> nodes = new ArrayList<ActorRef>();

        // Create the set of actors
        for (int i = 0; i < numActors; i++) {
            ActorRef a = system.actorOf(Props.create(Peer.class)
                    .withDispatcher("akka.actor.my-pinned-dispatcher"), "node-" + i);
            nodes.add(a);
        }

        // Send the list of all actors.
        for (int i = 0; i < numActors; i++) {
            nodes.get(i).tell(new ListMessage(nodes), ActorRef.noSender());
        }

        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        }
        catch (IOException ioe) {}
        system.terminate();

    }
}

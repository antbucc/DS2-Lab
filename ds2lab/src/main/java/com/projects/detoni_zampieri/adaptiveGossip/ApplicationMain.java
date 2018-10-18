package com.projects.detoni_zampieri.adaptiveGossip;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationMain {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("reliable_broadcast");
        int numActors = 10;
        List<ActorRef> nodes = new ArrayList<ActorRef>();

        // Create the set of actors
        for (int i = 0; i < numActors; i++) {
            ActorRef a = system.actorOf(Props.create(GossipActor.class)
                    .withDispatcher("akka.actor.my-pinned-dispatcher"), "node-" + i);
            nodes.add(a);
        }

        // Send the list of all actors.
        for (int i = 0; i < numActors; i++) {
            nodes.get(i).tell(new ListMessage(nodes), ActorRef.noSender());
        }

        // Smartly manage the lifetime of the application.
        // Wait until we have no more messages and then press ENTER
        // to terminate the execution.
        try {
            System.out.println(">>> Wait for the chats to stop and press ENTER <<<");
            System.in.read();

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        }
        catch (IOException ioe) {}
        system.terminate();

    }


}

package com.projects.geloso.epidemics;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.example.ReliableBroadcast;
import com.projects.geloso.epidemics.actors.EpidemicActor;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        ActorSystem system = ActorSystem.create("EpidemicSystem");

        int N = 4;
        List<ActorRef> group = new ArrayList<>();
        for (int i = 1; i <= N; i++) {
            group.add(system.actorOf(EpidemicActor.props().withDispatcher("akka.actor.my-pinned-dispatcher"), "EAP" + String.valueOf(i)));
        }
        for (ActorRef p : group) {
            p.tell(new EpidemicActor.StartMessage(group), null);
        }

        group.get(0).tell(new ReliableBroadcast.BroadcastMessage("a"), null);

        system.whenTerminated().wait();
    }

}

package com.projects.geloso.epidemics;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.projects.geloso.epidemics.actors.EpidemicPushActor;
import com.projects.geloso.epidemics.messages.AssignMessage;
import com.projects.geloso.epidemics.messages.StartMessage;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Main {

    private static EpiType epidemicType = EpiType.PUSH;

    public static void main(String[] args) throws InterruptedException, TimeoutException {

        ActorSystem system = ActorSystem.create("EpidemicSystem");

        int N = 4;
        List<ActorRef> group = new ArrayList<>();
        for (int i = 1; i <= N; i++) {
            switch (epidemicType) {
                case PUSH: {
                    group.add(system.actorOf(EpidemicPushActor.props().withDispatcher("akka.actor.my-pinned-dispatcher"), "EAP" + String.valueOf(i)));
                    break;
                }
            }
        }

        for (ActorRef p : group) {
            p.tell(new StartMessage(group), ActorRef.noSender());
        }

        group.get(0).tell(new AssignMessage("a"), null);

        Await.ready(system.whenTerminated(), Duration.Inf());
    }

    private enum EpiType {PUSH, PULL, PUSHPULL}

}

package com.projects.geloso.epidemics;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.projects.geloso.epidemics.actors.EpidemicPullActor;
import com.projects.geloso.epidemics.actors.EpidemicPushActor;
import com.projects.geloso.epidemics.actors.EpidemicPushPullActor;
import com.projects.geloso.epidemics.messages.AssignMessage;
import com.projects.geloso.epidemics.messages.StartMessage;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws InterruptedException, TimeoutException {

        ActorSystem system = ActorSystem.create("EpidemicSystem");
        String dispatcherId = "akka.actor.my-pinned-dispatcher";
        String actorName = "EPA%s";

        final String epidemic_type = System.getenv("EPIDEMIC_TYPE");
        final EpiType epiType = EpiType.valueOf(epidemic_type);

        int actorNumbers = 4;
        List<ActorRef> group = new ArrayList<>();
        for (int i = 1; i <= actorNumbers; i++) {
            switch (epiType) {
                case PUSH: {
                    group.add(system.actorOf(EpidemicPushActor.props().withDispatcher(dispatcherId), String.format(actorName, String.valueOf(i))));
                    break;
                }
                case PULL: {
                    group.add(system.actorOf(EpidemicPullActor.props().withDispatcher(dispatcherId), String.format(actorName, String.valueOf(i))));
                    break;
                }
                case PUSHPULL: {
                    group.add(system.actorOf(EpidemicPushPullActor.props().withDispatcher(dispatcherId), String.format(actorName, String.valueOf(i))));
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

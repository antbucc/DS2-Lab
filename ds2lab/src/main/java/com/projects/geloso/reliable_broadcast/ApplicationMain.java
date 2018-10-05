package com.projects.geloso.reliable_broadcast;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.projects.geloso.reliable_broadcast.messages.AppMessage;
import com.projects.geloso.reliable_broadcast.messages.StartMessage;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ApplicationMain {

    public static void main(String[] args) throws InterruptedException, TimeoutException {

        ActorSystem system = ActorSystem.create("MyActorSystem");

        int N = 4;
        List<ActorRef> ps = new ArrayList<ActorRef>();
        for (int i = 1; i <= N; i++) {
            ps.add(system.actorOf(RBActor.props().withDispatcher("akka.actor.my-pinned-dispatcher"), "RB" + String.valueOf(i)));
        }
        for (ActorRef p : ps) {
            p.tell(new StartMessage(ps), null);
        }

        ps.get(0).tell(new AppMessage(1), null);
      /*  try {
        	Thread.sleep(200);
        } catch (Exception e) {
        	e.printStackTrace();
        }

        /*ps.get(0).tell(new RBActor.BroadcastMessage("b"), null);
        try {
        	Thread.sleep(200);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        ps.get(2).tell(new RBActor.BroadcastMessage("x"), null);
        try {
        	Thread.sleep(100);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        ps.get(0).tell(new RBActor.BroadcastMessage("z"), null);
        */

        //ActorRef pingActor = system.actorOf(PingActor.props(), "pingActor");
        //pingActor.tell(new PingActor.Initialize(), null);
        // This example app will ping pong 3 times and thereafter terminate the ActorSystem -
        // see counter logic in PingActor
        Await.ready(system.whenTerminated(), Duration.Inf());
    }

}
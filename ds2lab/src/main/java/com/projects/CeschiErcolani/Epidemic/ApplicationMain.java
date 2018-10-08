/*
    Alberto Ercolani - 194431
    Lara Ceschi - 194427
*/


package com.projects.CeschiErcolani.Epidemic;

import java.util.ArrayList;
import java.util.List;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Timestamp;

public class ApplicationMain {

    public static void main(String args[]) {
        int N = 4;
        ActorSystem asSystem = ActorSystem.create("EpidemicSystem");

        List<ActorRef> lGroup = new ArrayList<ActorRef>();
        for (int i = 1; i <= N; i++) {
            System.out.println("Starting peer number " + i);
            lGroup.add(
                    asSystem.actorOf(
                            Epidemic.props().withDispatcher("akka.actor.my-pinned-dispatcher"), "EP" + String.valueOf(i)
                    )
            );
        }

        for (ActorRef arActor : lGroup) {
            arActor.tell(new Epidemic.HelloMsg(lGroup, AntiEntropyType.PUSH), null);
        }

        Random rRand = new Random();
        for (int i = 0; i < 12; i++) {
            try {
                int iNewValue = rRand.nextInt(100);
                ActorRef arActor = lGroup.get(rRand.nextInt(lGroup.size()));
                System.out.println("Telling " + arActor + " value = " + iNewValue);
                arActor.tell(new AntiEntropyMessage.UpdateMsg(new Timestamp(System.currentTimeMillis()), iNewValue), null);
                Thread.sleep(20 * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ApplicationMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

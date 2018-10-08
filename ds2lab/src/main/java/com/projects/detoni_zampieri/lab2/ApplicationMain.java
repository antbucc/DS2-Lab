package com.projects.detoni_zampieri.lab2;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.projects.detoni_zampieri.lab2.actor.PullActor;
import com.projects.detoni_zampieri.lab2.actor.PushActor;
import com.projects.detoni_zampieri.lab2.actor.PushPullActor;
import com.projects.detoni_zampieri.lab2.message.ActorListMessage;

import java.util.ArrayList;
import java.util.List;

public class ApplicationMain {

    enum EpidemicUpdateType {PUSH, PULL, PUSHPULL}
    static EpidemicUpdateType updateType = EpidemicUpdateType.PUSH;

    /*Return the correct instance of the actors*/
    static public Props getProps()
    {
        switch (updateType)
        {
            case PUSH:
                return PushActor.props().withDispatcher("akka.actor.my-pinned-dispatcher");
            case PULL:
                return PullActor.props().withDispatcher("akka.actor.my-pinned-dispatcher");
            case PUSHPULL:
                return PushPullActor.props().withDispatcher("akka.actor.my-pinned-dispatcher");
            default:
                System.out.println("Unknown updateType!");
                return null;
        }
    }

    public static void main(String[] args) {
    	
    	ActorSystem system = ActorSystem.create("MyActorSystem");
        
        int N = 4;
        ArrayList<ActorRef> ps = new ArrayList<ActorRef>();

        for (int i = 1; i <= N; i++) {
        	ps.add(system.actorOf(getProps(), "RB" + String.valueOf(i)));
        }

        /* Send the entire list of peers to everybody*/
        for (ActorRef p : ps) {
        	p.tell(new ActorListMessage(ps), null);
        }
    }

} 
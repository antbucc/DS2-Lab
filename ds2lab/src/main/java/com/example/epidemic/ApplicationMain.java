package com.example.epidemic;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class ApplicationMain {
	
	private static enum EpiType {PUSH, PULL, PUSHPULL};
	private static EpiType epiType = EpiType.PUSHPULL;
	private static Props getProps() {
		switch (epiType) {
		case PULL:
			return EpidemicPullActor.props().withDispatcher("akka.actor.my-pinned-dispatcher");
		case PUSHPULL:
			return EpidemicPushPullActor.props().withDispatcher("akka.actor.my-pinned-dispatcher");
		case PUSH:
		default:
			return EpidemicPushActor.props().withDispatcher("akka.actor.my-pinned-dispatcher");
		}
	}

    @SuppressWarnings("deprecation")
	public static void main(String[] args) {
    	
    	ActorSystem system = ActorSystem.create("MyActorSystem");
        
        int N = 2;
        List<ActorRef> ps = new ArrayList<ActorRef>();
        for (int i = 1; i <= N; i++) {
        	ps.add(system.actorOf(getProps(), "AEP-" + String.valueOf(i)));
        }
        
        for (ActorRef p : ps) {
        	p.tell(new EpidemicActor.StartMessage(ps), null);
        }
        
        ps.get(0).tell(new EpidemicActor.AssignMessage("a"), null);
        
        system.awaitTermination();
    }

}
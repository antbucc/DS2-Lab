package com.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;

public class GnutellaActor extends UntypedActor {
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public static Props props() {
		return Props.create(GnutellaActor.class);
	}
	
	public static class TestStability {
		public boolean next = true;
		
		public TestStability(boolean next) {
			this.next = next;
		}
	}
	
	
    public static class StartMessage {
    	protected final ActorRef initPeer;
    	protected final int id;
		
        public StartMessage(ActorRef ip, int id) {
            this.initPeer = ip;
            this.id = id;
        }
        
        public ActorRef getInitPeer() {
        	return initPeer;
        }
        
        public int getID() {
        	return id;
        }
    }
    
    public static class GnutellaPingMessage {
		/* to implement */
    }
    
    public static class GnutellaPongMessage {
    	/* to implement */
    }
    
    protected void insertPeer(int pID, ActorRef peer) {
    	if (!peerMap.containsKey(pID)) {
    		peerMap.put(pID, peer);
    		log.info("node {} add {}", myID, pID);
    	}
    }
    
    protected Map<Integer, ActorRef> peerMap = new HashMap<>();
    protected int myID = 0;
    public static final int ITTL = 0;

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof StartMessage) {
			/* to implement */
		} else if (message instanceof GnutellaPingMessage) {
			/* to implement */
		} else if (message instanceof GnutellaPongMessage) {
			/* to implement */
		} else if (message instanceof TestStability) {
			String peers = "";
			for (int p : peerMap.keySet()) {
				peers += " " + p;
			}
			log.info("node {} -> {}", myID, peers);
		} else {
            unhandled(message);
        }
	}
}

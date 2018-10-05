package noname.rbroadcast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Node extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private List<ActorRef> processes = new ArrayList<ActorRef>();

    public static Props props() {
        return Props.create(Node.class);
    }
    
    // The StartMessage from the main function will tell a process about the peers
    public static class StartMessage {
    	private final List<ActorRef> group;
		
        public StartMessage(List<ActorRef> group) {
            this.group = Collections.unmodifiableList(group);
        }
    }
    
    public static class BroadcastMessage {
    	private String message;
		
        public BroadcastMessage(String _message) {
            this.message = _message;
        }
    }
      
    
	public void onReceive(final Object message) throws Exception {
		if (message instanceof StartMessage) {
			
			// Set the peer list
        	StartMessage sm = (StartMessage) message;
        	processes = sm.group;
        	
        } else {
            unhandled(message);
        }
    }
}
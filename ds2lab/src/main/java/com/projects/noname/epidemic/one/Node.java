package noname.epidemic.one;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Node extends UntypedActor {
	
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private static Random rand = new Random();
    private List<ActorRef> processes;
    private Update myUpdate;
    private int type = 0;
    
    public static Props props() {
        return Props.create(Node.class);
    }
    
    // The StartMessage from the main function will tell a process about the peers
    public static class StartMessage {
    	private final List<ActorRef> group;
		private final int type;
    	
        public StartMessage(List<ActorRef> _group, int _type) {
            this.group = Collections.unmodifiableList(_group);
            this.type = _type;
        }
    }
	
    // The StartMessage from the main function will tell a process to store an update
    public static class AssignUpdate{
    	private Update msgUpdate;
    	
    	public AssignUpdate(Update _setUpdate) {
    		this.msgUpdate = _setUpdate;
    	}
    }
    
    // The PushUpdate from other Nodes force us to get infected
    public static class PushUpdate{
    	private Update msgUpdate;
    	private ActorRef sender;
    	
    	public PushUpdate(ActorRef _sender, Update _setUpdate) {
    		this.msgUpdate = _setUpdate;
    		this.sender = _sender;
    	}
    }
    
    // The PullUpdate is a request to be infected from other Nodes
    public static class PullUpdate{
    	private ActorRef sender;
    	
    	public PullUpdate(ActorRef _sender) {
    		this.sender = _sender;
    	}
    }
    
    // The PushPullUpdate from other Nodes force us to get infected and is a request to be infected from other Nodes
    public static class PushPullUpdate{
    	private Update msgUpdate;
    	private ActorRef sender;
    	
    	public PushPullUpdate(ActorRef _sender, Update _setUpdate) {
    		this.msgUpdate = _setUpdate;
    		this.sender = _sender;
    	}
    }
    
    public void round() {
    	switch(type) {
    	
    	// Push
    	case 0:
    		{
    			PushUpdate up = new PushUpdate(this.getSelf(), myUpdate);
        		// Select some node and PushUpdate
    			int random_node = rand.nextInt(this.processes.size());
    			this.processes.get(random_node).tell(up, null);
    		}
    		break;
    		
    	// Pull
    	case 1:
    		{
    			PullUpdate up = new PullUpdate(this.getSelf());
    			// Select some node and PullUpdate
    			int random_node = rand.nextInt(this.processes.size());
    			this.processes.get(random_node).tell(up, null);
    		}
    		break;
    		
    	// PushPull
    	case 2:
	    	{
	    		PushPullUpdate up = new PushPullUpdate(this.getSelf(), myUpdate);
	    		// Select some node and PushPullUpdate
	    		int random_node = rand.nextInt(this.processes.size());
    			this.processes.get(random_node).tell(up, null);
			}
    		break;
    	}
    	
    	// Schedule next round TODO
    	{
    		Runnable task = new Runnable() {
    			public void run() { round(); }
    		};
    		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    		scheduler.schedule(task, 1, TimeUnit.SECONDS);
    		scheduler.shutdown();
    	}
    	
    }
    
	public void onReceive(final Object message) throws Exception {

		if (message instanceof StartMessage) {
			
			// Set the peer list
        	StartMessage sm = (StartMessage) message;
        	this.processes = new ArrayList<ActorRef>();
        	this.processes = sm.group;
        	this.type = sm.type;
        	
        	// Set default update
        	this.myUpdate = new Update(-1, "");
        	
        	round();
        	
		} else if (message instanceof AssignUpdate) {
			
			// Force the Node to store the Update
			AssignUpdate au = (AssignUpdate)message;
			this.myUpdate = au.msgUpdate;
			
		} else if (message instanceof PushUpdate) {
			
			PushUpdate au = (PushUpdate)message;
			
			// Save the update if it is more recent
			if(au.msgUpdate.getValue() > this.myUpdate.getValue())
				this.myUpdate = au.msgUpdate;
			
			System.out.println("Received Push {" + au.msgUpdate.getMessage() + "} " + 
								au.sender + " -> " + this.getSelf());
			
		} else if (message instanceof PushPullUpdate) {
			
			PushPullUpdate au = (PushPullUpdate)message;
			
			// Save the update if it is more recent
			if(au.msgUpdate.getValue() > this.myUpdate.getValue())
				this.myUpdate = au.msgUpdate;
			
			System.out.println("Received PushPull {" + au.msgUpdate.getMessage() + "} " + 
					au.sender + " -> " + this.getSelf());
			
			// Push your update to the sender
			((PushPullUpdate) message).sender.tell(new Node.PushUpdate(this.getSelf(), myUpdate), null);
			
		} else if (message instanceof PullUpdate) {
			
			System.out.println("Received Pull " + ((PullUpdate) message).sender + " -> " + this.getSelf());
			
			// Push the update to sender
			((PullUpdate) message).sender.tell(new Node.PushUpdate(this.getSelf(), myUpdate), null);
			
		} else {
            unhandled(message);
        }
    }
}
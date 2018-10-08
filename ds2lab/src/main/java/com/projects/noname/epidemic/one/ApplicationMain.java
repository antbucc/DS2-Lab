package noname.epidemic.one;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class ApplicationMain {
	
	public static void main(String[] args) {
		
		int N = 4; // Default Number of Peers
		
		if(args.length == 1) {
			N = Integer.parseInt(args[0]); // Custom Number on Peers
		}
		
		// Create a system of Nodes
		ActorSystem system = ActorSystem.create("GroupSystem");
        
        // Define a List of Nodes (aka Actors, aka Clients)
        List<ActorRef> ps = new ArrayList<ActorRef>();
        
        // Creates N Nodes and add them to the system
        for (int i = 1; i <= N; i++) {
        	ps.add(system.actorOf(Node.props().withDispatcher("akka.actor.my-pinned-dispatcher"), "Node" + String.valueOf(i)));
        }
        
        // Send to every node the node list
        for (ActorRef p : ps) {
        	
        	// Actually evey nodes are sending a self-message 
        	// Flag {0, 1, 2} means {Push, Pull, PushPull}
        	p.tell(new Node.StartMessage(ps, 1), null);
        }
        
        for(int i = 0; i < 200; i++) {
        	
        	// Dispose an update to a random node
        	int node = (new Random().nextInt(ps.size()));
	        Update up = new Update(i,Integer.toString(i));
	        ps.get(node).tell(new Node.AssignUpdate(up), null);
	        System.out.println("Assign Update [" + i + "] to Node" + (node+1));
	        
	        try{
	        	Thread.sleep(7000);
	        }catch(Exception e) {
	        	i = 300;
	        }
        }
	}
}

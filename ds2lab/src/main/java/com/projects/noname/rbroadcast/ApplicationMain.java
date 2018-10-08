package noname.rbroadcast;

import java.util.ArrayList;
import java.util.List;

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
        	p.tell(new Node.StartMessage(ps), null);
        }
        
        // Node 0
        ps.get(0).tell(new Node.BroadcastMessage("Msg 1"), null);
	}
}

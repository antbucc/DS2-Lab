package com.projects.ZanellaGjika.RBroadcast;



import akka.actor.ActorSystem;
import java.util.List;

import com.projects.ZanellaGjika.RBroadcast.ReliableBroadcast;

import java.util.ArrayList;
import akka.actor.ActorRef;

public class ApplicationMain {

	public static void main(String[] args) {
		
		
		//creazione del sistema degli actor
		ActorSystem sys = ActorSystem.create("MyActorSystem");
		
		//numero processi creati
		int N = 3;
		//lista delle referenze dei processi creati
        List<ActorRef> ps = new ArrayList<ActorRef>();
		
        //ciclo per creare tutti i processi
        for (int i = 1; i <= N; i++) {
        	//sys.actorOf metodo per creare il attore desiderato
        	//ReliableBroadcast è l'attore che vogliamo creare, per fare ciò dobbiamo invocare il metodo props()
        	//nel withDispatcher viene dato un parametro presente nel file di application.conf
        	ps.add(sys.actorOf(ReliableBroadcast.props().withDispatcher("akka.actor.my-pinned-dispatcher"), 
        			"RB" + String.valueOf(i)
        			));
        }
        
        
        //per tutti i attori creati inviamo un messaggio di inizio trasmissione
        for (ActorRef p : ps) {
        	//nello start message inviamo anche una copia della lista di tutte le referenze di tutti i processi
        	p.tell(new ReliableBroadcast.StartMessage(ps), null);
        	
        }

        
        //invio di un unico messaggio per vedere se funziona ? 
        ps.get(0).tell(new ReliableBroadcast.BroadcastMessage("a"), null);
	}

}

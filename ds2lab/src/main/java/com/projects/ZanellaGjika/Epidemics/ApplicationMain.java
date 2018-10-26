package com.projects.ZanellaGjika.Epidemics;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class ApplicationMain {

	
	private static enum EpiType {PUSH, PULL, PUSHPULL};
	private static EpiType epiType = EpiType.PUSH;
	
	
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
        	//in base al valore di epitype decido quale è l'attore che vogliamo creare, per fare ciò dobbiamo invocare il metodo props()
        	//nel withDispatcher viene dato un parametro presente nel file di application.conf
        	
        	if(epiType ==  EpiType.PUSH) {
	        	ps.add(sys.actorOf(EpidemicPushActor.props().withDispatcher("akka.actor.my-pinned-dispatcher"), 
	        			"RB" + String.valueOf(i)
	        			));
	        }else if(epiType == EpiType.PULL){
	        	ps.add(sys.actorOf(EpidemicPushActor.props().withDispatcher("akka.actor.my-pinned-dispatcher"), 
	        			"RB" + String.valueOf(i)
	        			));
	        }else if(epiType == EpiType.PUSHPULL){
	        	ps.add(sys.actorOf(EpidemicPushActor.props().withDispatcher("akka.actor.my-pinned-dispatcher"), 
	        			"RB" + String.valueOf(i)
	        			));
	        }
        	
        }
        
        
        //per tutti i attori creati inviamo un messaggio di inizio trasmissione
        for (ActorRef p : ps) {
        	//nello start message inviamo anche una copia della lista di tutte le referenze di tutti i processi
        	p.tell(new EpidemicActor.StartMessage(ps), null);
        	
        }

        //invio il primo messaggio per inizializzare l'epidemicValue
        ps.get(0).tell(new EpidemicActor.AssignMessagge("a"), null);
        
	}

}

package com.projects.ZanellaGjika.RBroadcast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ReliableBroadcast  extends UntypedActor{

	//lista delle referenze di tutti gli altri processi/attori
	List<ActorRef> referenze = new ArrayList<ActorRef>();
	
	//set per lo storage dei messaggi deliverati
	Set<BroadcastMessage> delivered = new HashSet<BroadcastMessage>();

	//classe per il messsaggio iniziale con al configurazione del sistema
	static public class StartMessage{
		List<ActorRef> lista;
		public StartMessage(List<ActorRef> ps) {
			lista = ps;
		}
	}
	
	//messaggio normale di broadcast
	static class BroadcastMessage{
		String messaggio;
		public BroadcastMessage(String input) {
			messaggio = input;
		}
		
	}
	
	
	void onStartMessage(StartMessage mess) {
		//mi salvo nella classe la lista delle referenze
		referenze = mess.lista;
	}
	
	
	void onBroadcastMessagge(BroadcastMessage mess, ActorRef me, ActorRef sender) {
		//controllo se il messaggio è presente nella lista dei deliverati
		if(!delivered.contains(mess)) {
			//per tutti gli attori che non sono io e il sender glielo invio
			for(ActorRef act : referenze) {
				if(!act.equals(me) && !act.equals(sender)) {
					act.tell(mess, getSelf());
					//una volta inviato lo Rdelivero
					Rdeliver(mess);
				}
			}
		}
		
	}
	
	void Rdeliver(BroadcastMessage mess) {
		//stampo che ho deliverato il messaggio e lo metto nella coda dei delivered
		if(!delivered.contains(mess)) {
			System.out.println("Delivering messaggio : " + mess.messaggio + " \n");
			delivered.add(mess);
		}
	}
	
	//metodo standard
	public static Props props() {
        return Props.create(ReliableBroadcast.class);
    }
	
	
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		
		//alla ricezione dei messaggi dividiamo le casistiche
		if (message instanceof StartMessage) {
			onStartMessage((StartMessage)message);
		}
		else if(message instanceof BroadcastMessage) {
			onBroadcastMessagge((BroadcastMessage)message, getSelf(), getSender());
		}
		else {
			//errore nei messaggi
		}
	}
	
	
	
}

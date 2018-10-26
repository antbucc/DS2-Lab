package com.projects.ZanellaGjika.Epidemics;

import akka.actor.ActorRef;
import akka.actor.Props;

public class EpidemicPushActor extends EpidemicActor{
	
	
	//pros per creare la classe actor
	public static Props props() {
        return Props.create(EpidemicPushActor.class);
    }
	
	@Override
	protected void onEpidemicReceiveImpl(EpidemicMessage message) {
		//controllo che il messaggio che mi sia arrivato sia più recente
		//se è cosi lo salvo nel mio valore
		if(getValue().timestamp < message.value.timestamp) {
			System.out.println(getSelf() + " :: arrivato il messagio contente" + message.value.value + " con timestampo " + message.value.timestamp + " e mi aggiorno");
			getValue().copy(message.value);
		}
	}
	
	
	@Override
	protected void onEpidemicTimeoutImpl() {
		System.out.println("processazione timeout");
		//prendo random un processo
		ActorRef p = randomProcess();
		//creo il messaggio con all'interno il mio valore
		EpidemicMessage em = new EpidemicMessage();
		em.setValue(getValue());
		//mando il messaggio
		p.tell(em, getSelf());
		//resetto il timeout
		setEpidemicTimeOut();
	}
	
	
	
	
}

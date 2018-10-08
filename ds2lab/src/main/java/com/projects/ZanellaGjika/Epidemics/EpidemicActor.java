package com.projects.ZanellaGjika.Epidemics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;




public class EpidemicActor extends UntypedActor{

	//pros per creare la classe actor
	public static Props props() {
        return Props.create(EpidemicActor.class);
    }
	
	//la classe che memorizza il valore epidemico da memorizzare e di cui
	//fare la propagazione, qui è storato il valore(Stringa) e il timestamp a cui è collegato
	public static class EpidemicValue{
		protected long timestamp = -1;
		protected String value = null;
		public EpidemicValue(long timestamp, String value) {
			this.timestamp = timestamp;
			this.value = value;
		}
		public EpidemicValue(EpidemicValue v) {
			this.value = v.getValue();
			this.timestamp = v.getTimestamp();
		}
		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public void copy(EpidemicValue v) {
			this.value = v.getValue();
			this.timestamp = v.getTimestamp();
		}
	}
	
	//messaggio per la diffusione del valore aggiornato
	public static class EpidemicMessage{
		protected EpidemicValue value = new EpidemicValue(0, null);
		public EpidemicValue getValue() {
			return value;
		}
		public void setValue(EpidemicValue v) {
			this.value.copy(v);
		}
	}
	
	//messaggio per inizializzare gli attori con la lista di tutti gli attori
	//presenti nel sistema
	public static class StartMessage{
		List<ActorRef> lista;
		public StartMessage(List<ActorRef> list) {
			lista = list;
		}
		public List<ActorRef> getLista(){
			return lista;
		}
		
	}
	
	//messaggio mandato dal main process per inizializzare la diffusione epidemica
	public static class AssignMessagge{
		private String text;
		public AssignMessagge(String text) {
			this.text = text;
		}
		public String getText() {
			return text;
		}
	}
	
	//variabile per tenere in mente il timer
	private long timeout = Long.MAX_VALUE;
	//il delta per definire il prossimo timeout
	private final long delta = 100;
	//referenze degli altri attori
	protected List<ActorRef> referenze = new ArrayList<ActorRef>();
	//variabile per ottenere l'indice random
	protected Random rand = new Random();
	//valore del nodo da tenere aggiornato
	private EpidemicValue value = new EpidemicValue(0, null);
	
	//funzioni per ottendere i valori di EpidemicValue 
	protected EpidemicValue getValue() { return value; }
	protected void setValue(EpidemicValue v) { this.value.copy(v); }

	
	//questo è il controller del timeout, un semplice thread che controlla che il tempo
	//corrente sia minore del timeout, se non lo è esegue ciò che l'implementazione scelta è
	//atta a svolgere
	private void runController() {
		Thread t = new Thread(new Runnable() {
			
			public void run() {
				while (true) {
					//System.out.println("thread parallelo, controllo timeout");
					//all'inizio del processo il valore di timeout è il massimo di quelli possibili
					if(System.currentTimeMillis() >= timeout) {
						//System.out.println("timeout avvenuto, avvio l'invio");
						onEpidemicTimeoutImpl();
						//set epidemic timeout
					}
					try {
						//piccolo sleep per non controllare troppo spesso
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		//start del processo
		t.start();
	}	
	
	//funzione per scegliere random un processo ed essere sicuri di non trovare se stesi
	protected ActorRef randomProcess() {
		int index = rand.nextInt(referenze.size());
		while(referenze.indexOf(getSelf()) == index) {
			index = rand.nextInt(referenze.size());
		}
		return referenze.get(index);
	}
	
	//settare il timeout con il corrente millisec + il delta definito da noi
	protected void setEpidemicTimeOut() {
		//System.out.println("set del timeout");
		timeout = System.currentTimeMillis() + delta;
	}
	
	
		
	//smistamento messaggi in arrivo
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof StartMessage) {
			//mi è arrivato lo start message con la lista
			StartMessage sm = (StartMessage) message;
			referenze = sm.lista;
			//alla aprtenza avvio il controller e setto il timeout
			System.out.println("peer avviato");
			setEpidemicTimeOut();
			runController();
		}else if (message instanceof EpidemicMessage) {
			System.out.println("arrivato messaggio di epidemico");
			EpidemicMessage em = (EpidemicMessage) message;
			onEpidemicReceiveImpl(em);
		}else if(message instanceof AssignMessagge) {
			System.out.println("arrivato messaggio di inizio");
			//mi è arrivato il messaggio dal processo main
			//faccio l'update del mio valore e setto il timeout
			AssignMessagge as = (AssignMessagge) message;
			getValue().setValue(as.getText());
			getValue().setTimestamp(System.currentTimeMillis());
			//valueSynced();
		}
		
	}
	
	//funzioni diverse in base alla implementazione PUSH PULL ecc
	protected void onEpidemicTimeoutImpl() {}
	protected void onEpidemicReceiveImpl(EpidemicMessage message) {}
	
	
}

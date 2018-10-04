package com.example.epidemic;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class EpidemicActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static Props props() {
        return Props.create(EpidemicActor.class);
    }
    
    /*
     * The StartMessage from the main function will tell a process about the peers
     */
    public static class StartMessage {
    	private final List<ActorRef> group;
		
        public StartMessage(List<ActorRef> group) {
            this.group = Collections.unmodifiableList(group);
        }
    }
    
    /*
     * The AssignMessage from the main function will tell a process to update the value
     */
    public static class AssignMessage {
    	private final String text;

		public AssignMessage(String text) {
			super();
			this.text = text;
		}

		public String getText() {
			return text;
		}
    }
	
    /*
	 * Method to generate random delays
	 */
    protected Random rand = new Random(System.currentTimeMillis());
    protected long randomDelay() {
		return (long) (1000 + rand.nextInt(9000));
	}
	
	protected List<ActorRef> processes = new ArrayList<ActorRef>();
	public void onReceive(Object message) throws Exception {
		if (message instanceof StartMessage) {
			
			/*
			 * Set the peer list
			 */
        	StartMessage sm = (StartMessage) message;
        	processes = sm.group;
        	setEpidemicTimeOut();
        	runSchedule();
		} else if (message instanceof AssignMessage) {
			AssignMessage am = (AssignMessage) message;
			getValue().setValue(am.getText());
			getValue().setTimestamp(System.currentTimeMillis());
			valueSynced();
		} else if (message instanceof EpidemicMessage) {
			EpidemicMessage em = (EpidemicMessage) message;
			onEpidemicReceive(em);
		} else {
            unhandled(message);
        }
    }
	
	public static class EpidemicValue {
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
	
	public static class EpidemicMessage {
		protected EpidemicValue value = new EpidemicValue(0, null);

		public EpidemicValue getValue() {
			return value;
		}

		public void setValue(EpidemicValue v) {
			this.value.copy(v);
		}
	}
	
	protected ActorRef randomProcess() {
		int index = rand.nextInt(processes.size());
		while (processes.indexOf(getSelf()) == index) {
			index = rand.nextInt(processes.size());
		}
		return processes.get(rand.nextInt(processes.size()));
	}
	
	private EpidemicValue value = new EpidemicValue(0, null);
	
	protected EpidemicValue getValue() {
		synchronized(value) {
			return value;
		}
	}

	protected void setValue(EpidemicValue v) {
		synchronized (value) {
			this.value.copy(v);
		}
	}
	
	protected final ActorRef me = getSelf();
	
	private long timeout = Long.MAX_VALUE;
	
	private final long delta = 100;
	
	protected void setEpidemicTimeOut() {
		timeout = System.currentTimeMillis() + delta;
		//log.info("New timeout is {}", timeout);
	}
	
	int round = 0;
	private void runSchedule() {
		log.info("Run schedule now");
		Thread t = new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					if (System.currentTimeMillis() >= timeout) {
						onEpidemicTimeout();
						round++;
						setEpidemicTimeOut();
					}
				}
			}
		});
		t.start();
	}
	
	protected void valueSynced() {
		log.info("Current value is \"{}\" at round {}", getValue().getValue(), round);
		valueSyncedImpl();
	}
	protected void onEpidemicTimeout() {
		onEpidemicTimeoutImpl();
	}
	protected void onEpidemicReceive(EpidemicMessage message) {
		onEpidemicReceiveImpl(message);
	}
	
	protected void valueSyncedImpl() {
	}
	protected void onEpidemicTimeoutImpl() {
	}
	protected void onEpidemicReceiveImpl(EpidemicMessage message) {
	}
}
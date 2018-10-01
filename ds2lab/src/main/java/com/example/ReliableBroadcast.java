
package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ReliableBroadcast extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static Props props() {
        return Props.create(ReliableBroadcast.class);
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
     * The BroadcastMessage from the main function will tell a process to broadcast to all peers
     */
    public static class BroadcastMessage {
    	private final String text;
    	
    	public BroadcastMessage(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
    
    /*
     * The RBMessage is the message used in ReliableBroadcast 
     * so when a process receive this message, it should relay the message to other peers
     */
    public static class RBMessage {
        protected final String text;

        public RBMessage(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((text == null) ? 0 : text.hashCode());
			return result;
		}
		
		/*
		 * The equals method define how we want to identify message as identical, 
		 * in this case if the text is the same then we consider the message the same
		 * We use this to check if a message is already in the delivered list
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RBMessage other = (RBMessage) obj;
			if (text == null) {
				if (other.text != null)
					return false;
			} else if (!text.equals(other.text))
				return false;
			return true;
		}
    }
    
    /*
     * A FIFOMessage contains extra information when you want to guarantee the FIFO Order
     * sender is an ActorRef to the original broadcaster
     * seqn is the sequence number of the incoming message (int)
     * So we can always tell the order of the messages that come from the same sender 
     * - and organize the message delivery with sequence number
     */
    public static class FIFOMessage extends RBMessage {
    	private final ActorRef sender;
    	private final int seqn;
		public FIFOMessage(ActorRef sender, int seqn, String text) {
			super(text);
			this.sender = sender;
			this.seqn = seqn;
		}
		public ActorRef getSender() {
			return sender;
		}
		public int getSeqn() {
			return seqn;
		}
		public void print() {
			System.out.println(sender.path().name() + ":" + seqn + ":" + text);
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((sender == null) ? 0 : sender.hashCode());
			result = prime * result + seqn;
			return result;
		}
		
		/*
		 * Two FIFOMessage s are considered equal if they have the same sender and seqn
		 * As now the text is not so important we skip comparing it in this method
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FIFOMessage other = (FIFOMessage) obj;
			if (sender == null) {
				if (other.sender != null)
					return false;
			} else if (!sender.equals(other.sender))
				return false;
			if (seqn != other.seqn)
				return false;
			return true;
		}
    }
    
    /*
     * With the non-blocking version, a CNBMessage is tagged with the recent history 
     * so the extra information should be a list of recent CNBMessage s
     * Note that we compare messages as in FIFOMessage so we skip it and inherit from FIFOMessage
     */
    public static class CNBMessage extends FIFOMessage {
    	private final List<CNBMessage> recent;

		public CNBMessage(ActorRef sender, int seqn, String text, List<CNBMessage> recent) {
			super(sender, seqn, text);
			this.recent = recent;
		}

		public List<CNBMessage> getRecent() {
			return recent;
		}
    }
    
    /*
     * With the blocking version, we need a vector of timestamps inside the CBMessage
     * Note that we compare messages as in FIFOMessage so we skip it and inherit from FIFOMessage
     */
    public static class CBMessage extends FIFOMessage {
    	private final int[] TS;

		public CBMessage(ActorRef sender, int seqn, String text, int[] vC) {
			super(sender, seqn, text);
			/*
			 * COPY and NOT ASSIGN
			 */
			TS = new int[vC.length];
			for (int i = 0; i < TS.length; i++) {
				TS[i] = vC[i];
			}
		}
		
		public void print() {
			String ts = "";
			for (int t : TS) {
				ts += " " + t;
			}
			System.out.println(getText() + ":" + ts);
		}

		public int[] getTS() {
			return TS;
		}
    }
    
	// The list of peers is kept here
    private List<ActorRef> processes = new ArrayList<ActorRef>();
    
    /*
     * Here is a nice debug function to print and see the broadcaster
     */
    public void Rbroadcast(Object message) {
    	BroadcastMessage m = (BroadcastMessage) message;
        log.info("broadcast message \"{}\"", m.getText());
	}
    
    /*
     * The implementation of ReliableBroadcast requires a list of delivered message,
     * so here it is
     */
    private List<RBMessage> delivered = new ArrayList<RBMessage>();
    
    /*
     * The R-deliver of ReliableBroadcast is implemented here
     * Basically it just print a log to notify it delivers the message
     * But if we go on and implement FIFO Order, it will call the FIFOOrder function and pass along the message
     * As we try we will see that R-deliver will be shown at random order 
     * but F-deliver will repsect the FIFO order
     */
	public void Rdeliver(Object message,  ActorRef me, ActorRef sender) throws Exception {
		RBMessage m = (RBMessage) message;
		log.info("R delivered message \"{}\" from {}", m.getText(), sender.path().name());
		if (simulateFailureFIFO) {
			FIFOOrder(message, me, sender);
		}
	}
	
	/*
	 * The implementation of handling incoming message in ReliableBroadcast
	 * We simply relay any new message to all other peers
	 */
	public void NoOrder(Object message, ActorRef me, ActorRef sender) throws Exception {
		// only for new message
		if (!delivered.contains(message)) {
        	for (ActorRef p : processes) { // go through all processes
        		if (!p.equals(me) && !p.equals(sender)) { // exclude itself and the sender
        			p.tell(message, me); // send the message point-2-point
        			Rdeliver(message, me, sender); // R-deliver the message
        			// add the message into delivered so it will not be delivered again
        			delivered.add((RBMessage) message); 
        		}
        	}
        }
	}
	
	/*
	 * To implement FIFO Order we need
	 * a buffer to queue all messages in case we receive a message from a sender and the seqn number is wrong
	 * e.g. we are expecting seqn = 2 but receive seqn = 3, just queue it in the buffer and process later
	 * an int array recvnext to store the expecting seqn for each sender
	 * an int counter sendnext to keep track of the seqn that the process is sending to other processes
	 */
	List<FIFOMessage> buffer = new ArrayList<FIFOMessage>();
	int[] recvnext = null;
	int sendnext = 0;
	
	/*
	 * The initialization of FIFO Order requires the number of peers 
	 * so we defer the init of recvnext in this method
	 */
	private void initFIFOOrder() {
		buffer.clear();
		sendnext = 0;
		recvnext = new int[processes.size()];
		for (int i = 0; i < recvnext.length; i++) {
			recvnext[i] = 1;
		}
	}
	
	/*
	 * Given a message m, we look through the buffer
	 * and see if there is an m' that with the same sender
	 * but the seqn is a correct one
	 * e.g. if we receive seqn = 3 before seqn = 2, 
	 * this will return null when seqn = 3 comes, so keep the message in the buffer and do nothing
	 * but next time, when seqn = 2 comes, this will return seqn = 2 first
	 * then the next loop, seqn = 3 will be delivered
	 */
	private FIFOMessage fetchFIFOMessage(ActorRef sender) {
		for (FIFOMessage fm : buffer) {
			if (fm.getSender().equals(sender) && fm.getSeqn() == recvnext[processes.indexOf(sender)]) {
				return fm;
			}
		}
		return null;
	}
	
	/*
	 * The receive function of FIFOBroadcast
	 */
	public void FIFOOrder(Object message, ActorRef me, ActorRef sender) throws Exception {
		FIFOMessage fm = (FIFOMessage) message;
		/*
		 * add the new message into buffer
		 * remember that this is called by R-deliver so this is always a new message
		 */
		buffer.add(fm); 
		FIFOMessage m = fetchFIFOMessage(sender); // get the FIFOMessage to process
		while(m != null) {
			Fdeliver(m, me, sender); // F-deliver
			recvnext[processes.indexOf(sender)]++; // increase the expecting seqn from the sender
			buffer.remove(m); // remove the delivered message from buffer
			m = fetchFIFOMessage(sender); // get from the buffer again until m is null
		}
	}
	
	/*
     * The F-deliver of FIFOBroadcast is implemented here
     * Basically it just print a log to notify it F-delivers the message
     * But if we go on and implement Causal Order, it will call the CausalOrder function and pass along the message
     * As we try we will see that F-deliver will be shown out of causal order 
     * but CB/CNB-deliver will repsect the causal order
     */
	private void Fdeliver(Object message, ActorRef me, ActorRef sender) throws Exception {
		RBMessage m = (RBMessage) message;
		log.info("F delivered message \"{}\" from {}", m.getText(), sender.path().name());
		if (useCausalOrder) {
			CausalOrder(message, me, sender);
		}
	}
	
	/*
	 * The blocking version of CausalBroadcast is implemented here
	 * We need the following extra information
	 * cbuffer is a buffer for the causal ordering of messages
	 * cVC is a vector of int specify the current vector clock of the process
	 * and cVC should be COPIED to the message timestamp (TS)
	 * DO NOT PASS THE REFERENCE AS WE MAY UPDATE THE TS WHEN WE UPDATE cVC
	 */
	private List<CBMessage> cbuffer = new ArrayList<CBMessage>();
	private int[] cVC = null;
	
	private void initCausalOrderBlocking() {
		cbuffer.clear();
		cVC = new int[processes.size()];
		for (int i = 0; i < cVC.length; i++) {
			cVC[i] = 0;
		}
	}
	
	private void printVC() {
		String vc = "";
		for (int c : cVC) {
			vc += " " + c;
		}
		System.out.println(getSelf().path().name() + ":" + vc);
	}
	
	/*
	 * Similar to FIFOBroadcast, we fetch the suitable message from cbuffer
	 * To identify the message, we compare the current vector clock cVC and the message timestamp TS
	 */
	private CBMessage fetchPrevCBMessage(ActorRef sender) {
		for (CBMessage c : cbuffer) {
			// First it must be the next message from the sender
			if (cVC[processes.indexOf(sender)] == c.getTS()[processes.indexOf(sender)] - 1) {
				boolean flag = true;
				for (ActorRef p : processes) {
					if (!p.equals(sender)) {
						// Then no other process has a later clock than the message
						if (cVC[processes.indexOf(p)] < c.getTS()[processes.indexOf(p)]) {
							flag = false;
							break;
						}
					}
				}
				
				if (flag) {
					return c;
				}
			}
		}
		return null;
	}
	
	/*
	 * Refer to http://disi.unitn.it/~montreso/ds/handouts/03-gpe.pdf
	 * Slide 34/96
	 * For the implementation of vector clock, this is the update when receiving a message
	 */
	private void updateVC(CBMessage message, ActorRef me, ActorRef sender) {
		for (int i = 0; i < processes.size(); i++) {
			if (processes.get(i).equals(me)) {
				cVC[i]++;
			} else {
				if (message.getTS()[i] > cVC[i]) {
					cVC[i] = message.getTS()[i];
				}
			}
		}
		//printVC();
	}
	
	/*
	 * This implements the receive function of Causal Order blocking version
	 */
	public void CausalOrderBlocking(Object message, ActorRef me, ActorRef sender) throws Exception {
		CBMessage cm = (CBMessage) message;
		
		//cm.print();
		//printVC();
		
		cbuffer.add(cm);
		CBMessage m = fetchPrevCBMessage(sender);
		while (m != null) {
			CBdeliver(m, me, sender);
			updateVC(m, me, sender);
			cbuffer.remove(m);
			m = fetchPrevCBMessage(sender);
		}
	}
	
	/*
	 * C-deliver blocking version
	 */
	private void CBdeliver(Object message, ActorRef me, ActorRef sender) throws Exception {
		CBMessage m = (CBMessage) message;
		log.info("CB delivered message \"{}\" from {}", m.getText(), sender.path().name());
	}
	
	/*
	 * For the non-blocking version, 
	 * besides the list of C-delivered messages,
	 * we need a list of recent messages
	 * to tag into the message before sending as history, 
	 * Basically the history is a list of messages 
	 * that we receive from others between our two broadcasts
	 */
	private List<CNBMessage> cdelivered = new ArrayList<CNBMessage>();
	private List<CNBMessage> crecent = new ArrayList<CNBMessage>();
	
	/*
	 * Initialization as always
	 */
	private void initCausalOrderNonBlocking() {
		cdelivered.clear();
		crecent.clear();
	}
	
	/*
	 * And the implementation of the receive function of CausalBroadcast non-blocking version
	 */
	public void CausalOrderNonBlocking(Object message, ActorRef me, ActorRef sender) throws Exception {
		CNBMessage cm = (CNBMessage) message;
		cm.getRecent().add(cm);
		for (CNBMessage c : cm.getRecent()) {
			if (!cdelivered.contains(c)) {
				cdelivered.add(c);
				crecent.add(c);
				CNBdeliver(c, me, sender);
			}
		}
	}
	
	/*
	 * C-deliver non-blocking version
	 */
	private void CNBdeliver(Object message, ActorRef me, ActorRef sender) throws Exception {
		CNBMessage m = (CNBMessage) message;
		log.info("CNB delivered message \"{}\" from {}", m.getText(), sender.path().name());
	}
	
	/*
	 * We switch between the non and blocking versions with the flag isCausalBlocking
	 */
	public void CausalOrder(Object message, ActorRef me, ActorRef sender) throws Exception {
		if (isCausalBlocking) {
			CausalOrderBlocking(message, me, sender);
		} else {
			CausalOrderNonBlocking(message, me, sender);
		}
	}
	
	/*
	 * Various flags to switch algorithms
	 */
	/*
	 * We want to simulate FIFO failure (with random delays)
	 * to see the effect of the FIFO algorithm
	 */
	private boolean simulateFailureFIFO = false; 	
	
	/*
	 * We want to reverse the receiving order of messages
	 * Up to 100 messages, the first message will have longest delay
	 */
	private boolean simulateFailureFIFO100 = false;
	
	/*
	 * We go on to use the CausalBroadcast
	 * and we have 2 versions so isCausalBlocking
	 * is for switching between them
	 */
	private boolean useCausalOrder = false;
	private boolean isCausalBlocking = false;
	private Random rand = new Random(System.currentTimeMillis());
	
	/*
	 * Method to generate random delays
	 */
	private int counter = 100;
	private long randomDelay() {
		if (simulateFailureFIFO100) {
			return (long) 10*counter--;
		}
		return (long) (rand.nextInt(1000) + rand.nextInt(9000));
	}
	
	/*
	 * Handle 3 types of message:
	 * StartMessage: set the peer list internally
	 * BroadcastMessage: broadcast the incoming text
	 * so when a process receive a BroadcastMessage, 
	 * it takes out the text and broadcast RBMessage (or another subclass) to all others
	 * some other actions may be implemented here 
	 * (see the R-broadcast, F-broadcast, C-broadcast, etc.)
	 * RBMessage: receive and handle the message from a broadcaster
	 * and eventually deliver it, the receive function is called here
	 */
	public void onReceive(final Object message) throws Exception {
		if (message instanceof StartMessage) {
			
			/*
			 * Set the peer list
			 */
        	StartMessage sm = (StartMessage) message;
        	processes = sm.group;
        	
        	/*
        	 * Some initializations for different broadcast algorithms
        	 */
        	if (simulateFailureFIFO) {
        		initFIFOOrder();
        		if (useCausalOrder) {
        			if (isCausalBlocking) {
        				initCausalOrderBlocking();
        			} else {
        				initCausalOrderNonBlocking();
        			}
        		}
        	}
        	
		} else if (message instanceof BroadcastMessage) {
			
			BroadcastMessage bm = (BroadcastMessage) message;
			Rbroadcast(message); // just for the debugging purpose, print the broadcast log
			
			/*
			 * rm is an instance of RBMessage and can be used to referenced to any subclass
			 */
			RBMessage rm = null;
			
			if (simulateFailureFIFO) {
				
				/* 
				 * from FIFOOder onwards, we need to always increase this counter 
				 * for the seqn of the message
				 */
				sendnext++;
				
				/*
				 * CausalOrder non-blocking requires an update of the vector clock 
				 * before sending the message
				 */
				if(useCausalOrder && isCausalBlocking) {
					cVC[processes.indexOf(getSelf())]++;
				}
			}
			//--------------------------------------------------------------
			
			for (ActorRef p : processes) {
        		if (!p.equals(getSelf())) { // we don't send message to ourselves
        			if (simulateFailureFIFO) {
        				if (useCausalOrder) {
        					if (isCausalBlocking) {
        						/*
        						 * CausalOrder blocking version
        						 * just copy VC to TS
        						 */
        						rm = new CBMessage(getSelf(), sendnext, bm.getText(), cVC);
        					} else {
        						/*
        						 * CausalOrder non-blocking version
        						 * COPY the list crecent to cr
        						 * clear the list crecent after creating the message
        						 */
        						List<CNBMessage> cr = new ArrayList<CNBMessage>();
        						cr.addAll(crecent);
        						rm = new CNBMessage(getSelf(), sendnext, bm.getText(), cr);
        						crecent.clear();
        					}
        				} else {
        					/*
    						 * FIFOOrder
    						 */
        					rm = new FIFOMessage(getSelf(), sendnext, bm.getText());
        				}
        			} else {
        				/*
        				 * Simple ReliableBroadcast
        				 */
        				rm = new RBMessage(bm.getText());
        			}
        			p.tell(rm, getSelf()); // send the message rm
        		}
        	}
			
			/*
			 * R-deliver is called 
			 * to deliver to self
			 * this is ReliabelBroadcast
			 */
			Rdeliver(rm, getSelf(), getSelf());
			delivered.add(rm);
		} else if (message instanceof RBMessage) {
			
			/*
			 * me and sender are two constant references to send to the Thread
			 * inside the Thread, we cannot use getSelf() and getSender()
			 */
			final ActorRef me = getSelf();
			final ActorRef sender = getSender();
			
			if (simulateFailureFIFO) {
				
				Thread t = new Thread(new Runnable() {
					
					public void run() {
						try {
							Thread.sleep(randomDelay());
							NoOrder(message, me, sender);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	            t.start();
			} else {
				NoOrder(message, me, sender);
			}
        } else {
            unhandled(message);
        }
    }
}
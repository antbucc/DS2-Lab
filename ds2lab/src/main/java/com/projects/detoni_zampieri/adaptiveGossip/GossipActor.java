package com.projects.detoni_zampieri.adaptiveGossip;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import scala.concurrent.duration.FiniteDuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class GossipActor extends UntypedActor {

    public GossipActor() {

        // Initialize default variables
        this.rng = new Random();
        this.nodeId = rng.nextInt();

        this.events = new ArrayList<Event>();
        this.minBuffer = MAX_BUFFER_SIZE - this.events.size();
        this.k = 10;
        this.f = 3;
        this.T = 1500;
        this.s_timeout = this.T*2;
        this.h = 7;
        this.l = 5;
        this.avgAge = (this.h +this.l)/2.0;
        this.lost = new HashSet<>();
        this.alpha = 0.8;
        this.token_count = this.max_token_count = 10;
        this.token_rate = 1/300.0;  // 1 token every 1500 ms
        this.rh =0.05; // 5% increment
        this.rl=0.05; // 5% decrement
        this.W=0.5;
        
        this.delta = 2;
        this.s = 2;
        this.minBuffers = new ArrayList<Integer>();
        for (int i=0; i<delta; i++)
        {
            this.minBuffers.add(i, MAX_BUFFER_SIZE - this.events.size());
        }
        this.minBuffer = this.minBuffers.get(s-1);
        this.delayed_events = new ArrayList<Event>();

        // Logger: timestamp, variable, value
        try {
            this.logger = new PrintWriter(new File("./"+this.nodeId+"-log.csv"));
        } catch (FileNotFoundException e)
        {
            System.out.println(e);
        }
    }

    @Override
    public void onReceive(Object o) throws Exception {

        // Catch the list message
        if (o instanceof ListMessage)
        {
            this.peers = ((ListMessage) o).m_nodes;
            scheduleTimeout(new EnterNewPeriodMessage(),this.s_timeout); //initialise timeouts for periodic actions
            scheduleTimeout(new UpdateAgesAndGossipMessage(),this.T);
            scheduleTimeout(new IncrementToken(), (int)(1/this.token_rate));
        } else if (o instanceof UpdateAgesAndGossipMessage)
        {
            onUpdateAgeAndGossipMessage((UpdateAgesAndGossipMessage)o);
        } else if (o instanceof EnterNewPeriodMessage){

            this.s++;
            this.minBuffers.add(s-1, this.MAX_BUFFER_SIZE-this.events.size());
            this.minBuffer = this.minBuffers.get(s-1);

            for(int i=s-1; i>s-1-delta-1; i--)
            {
                if (this.minBuffers.get(i) < this.minBuffer)
                {
                    this.minBuffer = this.minBuffers.get(i);
                }
            }
            
            scheduleTimeout(new EnterNewPeriodMessage(),this.s_timeout);

        } else if (o instanceof Message) {
            onReceiveGossip((Message)o);
        } else if(o instanceof IncrementToken){
        	if(this.token_count < this.max_token_count) {
        		this.token_count++;
        	}
            scheduleTimeout(new IncrementToken(), (int)(1/this.token_rate));
        } else {
            unhandled(o);
        }
    }
    
    public void onUpdateAgeAndGossipMessage(UpdateAgesAndGossipMessage o) {
        // Increment age of events
        for (Event e : events)
        {
            e.incrementAge();
        }

        // Remove oldest elements
        this.events.removeIf(e -> e.age > k);

        // Wait to have enough tokens
        //System.out.println("Agent "+this.nodeId+": "+this.token_count);
        if (this.token_count > 0) {
            this.token_count--;

            // Add the newly generated event to the list
            this.delayed_events.add(o.newEvent);
            this.events.addAll(this.delayed_events);
            this.delayed_events.clear();

            // Send gossip to everybody
            gossipMulticast(new Message(new ArrayList<>(this.events),
                    this.s,
                    this.minBuffer));
        } else {
            this.delayed_events.add(o.newEvent);
        }

        scheduleTimeout(new UpdateAgesAndGossipMessage(),this.T); // reschedule the periodic task

        //throttle sender
        if(this.avgAge>this.h && this.rng.nextDouble()>this.W) {
        	this.token_rate *= 1+this.rh;
            this.log(System.currentTimeMillis(),
                    "token_rate",
                    1/this.token_rate);
        } else if(this.avgAge < this.l) {
        	this.token_rate *= 1-this.rl;
            this.log(System.currentTimeMillis(),
                    "token_rate",
                    1/this.token_rate);
        }
    }
    
    public void scheduleTimeout(Object timeoutMessage,int milliseconds) {
    	getContext().system().scheduler().scheduleOnce(
                new FiniteDuration(milliseconds, TimeUnit.MILLISECONDS),
                getSelf(),
                timeoutMessage,
                getContext().system().dispatcher(),
                getSelf()
        );
    }

    private void gossipMulticast(Message m) {
        // Select f random processes
        // and send them the message
        Collections.shuffle(this.peers);
        int skip = 0;

        Iterator<ActorRef> tmp_iter= this.peers.iterator();
        for (int i = 0; i < f; i++) {
           ActorRef p = tmp_iter.next();
           if (p.equals(getSelf()))
           {
               p= tmp_iter.next();
           }
           try {
               p.tell(m.clone(), ActorRef.noSender());
           } catch (Exception e) {
           }
        }
        //System.out.println("Agent "+this.nodeId+": Sent broadcast message.");
    }

    public Event getLocalEvent(Event e)
    {
        return this.events.get(this.events.indexOf(e));
    }

    public void onReceiveGossip(Message gossip)
    {
        //update my events
        for(Event e:gossip.events)
        {
            if(!this.events.contains(e))
            {
                this.events.add(e);
                deliver(e);
            }
            else {
                Event e_prime = getLocalEvent(e);
                if(e_prime.age < e.age)
                    e_prime.age = e.age;
            }
        }
        if(this.events.size() > this.MAX_BUFFER_SIZE)
        {
            // Remove the previous events
            this.lost.clear();

            //remove the excess
            ArrayList<Event> sorted_events = new ArrayList<>(this.events);
            Collections.sort(sorted_events,(e1,e2)-> e2.age - e1.age);
            Iterator<Event> iter = sorted_events.iterator();

            // Update the average age
            while(this.events.size()-this.lost.size() > this.minBuffer)
            {
                Event lost_e = iter.next();
                this.lost.add(lost_e);
                this.avgAge = this.alpha*this.avgAge + (1-this.alpha)*lost_e.age;
                this.log(System.currentTimeMillis(),
                        "avg_age",
                        this.avgAge);
            }

            // Remove the oldest elements
            Iterator<Event> iter_remove = sorted_events.iterator();
            while(this.events.size() > this.MAX_BUFFER_SIZE)
            {
                Event e = iter_remove.next();
                this.events.remove(e);
            }
            
        }

        // Update congestion rates
        if (gossip.age == this.s && gossip.minBuffer < this.minBuffers.get(s-1))
        {
            this.minBuffers.set(s-1, gossip.minBuffer);
        }

        // Synchronize
        if (gossip.age > this.s)
            this.s=gossip.age;

    }

    public void deliver(Event e)
    {
        //System.out.println("Received event "+e.id.toString());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        System.out.println("Actor "+nodeId+"-> MinBuffer("
                +minBuffer
                +"), ActualMinBuffer("
                +(this.MAX_BUFFER_SIZE-this.minBuffers.size())+")");
    }

    public void log(long tmstp, String variable, Object value)
    {

        StringBuilder builder = new StringBuilder();
        builder.append(tmstp+",");
        builder.append(variable+",");
        builder.append(value);
        builder.append("\n");
        this.logger.write(builder.toString());
        this.logger.flush();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.logger.close();
    }

    // Default variables for the actor
    public Random rng;
    public int nodeId;
    public List<ActorRef> peers;

    // Adaptive Gossip Variables
    private int MAX_BUFFER_SIZE = 10; // Max number of messages
    public List<Event> events; // Buffer for messages
    public int minBuffer; // Minimal size of the buffer
    public List<Integer> minBuffers;
    public int s; // Current period
    public int delta; // Interval for computing minBuffer
    public int k; // Maximum age for events
    public int f; // Total number of random peers (fanout)
    public int T; //timeout period for UpdateAndGossip procedure
    public int s_timeout; // timeout period for the generation of a new period
    public int h,l; // high and low parameters to vary the thorughput
    public double avgAge;  // average age statistic
    public double alpha; //hyperparameter for the exponential mean for the age of discarded events
    public Set<Event> lost;
    public int token_count;
    public int max_token_count;
    public double token_rate; //rate at which restore some token to be sent (token per millisecond)
    public double rh,rl; //modifiers in percentage that regulate the 'token_rate' variable
    public double W;
    public List<Event> delayed_events;

    public PrintWriter logger;
}

package com.projects.detoni_zampieri.adaptiveGossip;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import scala.concurrent.duration.FiniteDuration;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.projects.detoni_zampieri.lab1.message.StartBroadcastMessage;

public class GossipActor extends UntypedActor {

    public GossipActor() {

        // Initialize default variables
        this.rng = new Random();
        this.nodeId = rng.nextInt();

        this.events = new ArrayList<Event>();
        this.minBuffer = MAX_BUFFER_SIZE - this.events.size();
        this.k = 10;
        this.f = 3;
        this.s_timeout = 3000;
        this.T = 1300;
        this.h = 7;
        this.l = 5;
        this.avgAge = (this.h +this.l)/2;
        this.lost = new HashSet<>();
        this.alpha = 0.8;

        this.delta = 2;
        this.s = 2;
        this.minBuffers = new ArrayList<Integer>();
        for (int i=0; i<delta; i++)
        {
            this.minBuffers.add(i, MAX_BUFFER_SIZE - this.events.size());
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
        } else if (o instanceof UpdateAgesAndGossipMessage)
        {
            // Increment age of events
            for (Event e : events)
            {
                e.incrementAge();
            }

            // Remove oldest elements
            this.events.removeIf(e -> e.age > k);

            // Add the newly generated event to the list
            this.events.add(((UpdateAgesAndGossipMessage)o).newEvent);

            // Send gossip to everybody
            gossipMulticast(new Message(new ArrayList<>(this.events),
                    this.s,
                    this.minBuffer));
            
            scheduleTimeout(new UpdateAgesAndGossipMessage(),this.T); // reschedule the periodic task

        }else if (o instanceof EnterNewPeriodMessage){

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
        } else
        {
            unhandled(o);
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
        for (int i = 0; i < f; i++) {
            // Do not send a message to myself (h@ck3r w@y)
            if (this.peers.get(i+skip).equals(getSelf())) {
                skip++;
                i--;
            } else {
                this.peers.get(i + skip).tell(m, ActorRef.noSender());
            }
        }
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
            //remove the excess
            ArrayList<Event> sorted_events = new ArrayList<>(this.events);
            Collections.sort(sorted_events,(e1,e2)-> e2.age - e1.age);
            int diff = this.events.size() - MAX_BUFFER_SIZE;
            Iterator<Event> iter = sorted_events.iterator();
            for(int i=0;i<diff;i++)
            {
                Event lost_e = iter.next();
                iter.remove();
                this.lost.add(lost_e);
                this.avgAge = this.alpha*this.avgAge + (1-this.alpha)*lost_e.age;
            }
            
        }

        // Update congestion rates
        if (gossip.age == this.s && gossip.minBuffer < this.minBuffers.get(s-1))
        {
            this.minBuffers.set(s-1, gossip.minBuffer);
        }

    }

    public void deliver(Event e)
    {
        System.out.println("Received event "+e.id.toString());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        System.out.println("Actor "+nodeId+"-> MinBuffer("
                +minBuffer
                +"), ActualMinBuffer("
                +(this.MAX_BUFFER_SIZE-this.minBuffers.size())+")");
    }

    // Default variables for the actor
    public Random rng;
    public int nodeId;
    public List<ActorRef> peers;

    // Adaptive Gossip Variables
    private int MAX_BUFFER_SIZE = 100; // Max number of messages
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
}

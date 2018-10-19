package com.projects.detoni_zampieri.adaptiveGossip;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.projects.detoni_zampieri.lab1.message.StartBroadcastMessage;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GossipActor extends UntypedActor {

    public GossipActor() {

        // Initialize default variables
        this.rng = new Random();
        this.nodeId = rng.nextInt();

        this.events = new ArrayList<Event>();
        this.minBuffer = MAX_BUFFER_SIZE - this.events.size();

    }

    @Override
    public void onReceive(Object o) throws Exception {

        // Catch the list message
        if (o instanceof ListMessage)
        {
            this.peers = ((ListMessage) o).m_nodes;
        } else if (o instanceof UpdateAgesAndGossipMessage)
        {
            // Increment age of events
            for (Event e : events)
            {
                e.age++;
            }

            // Remove oldest elements
            for (Event e : events)
            {
                if (e.age > k)
                {
                    events.remove(e);
                }
            }

            // Send gossip to everybody
            gossipMulticast(new Message(Collections.unmodifiableList(this.events)));

        } else if (o instanceof Message) {
            onReceiveGossip((Message)o);
        } else
        {
            unhandled(o);
        }
    }

    private void gossipMulticast(Message m) {
        // Select f random processes
        // and send them the message
        Collections.shuffle(this.peers);
        int skip = 0;
        for (int i = 0; i < f; i++) {
            // Do not send a message to myself (h@ck3r w@y)
            if (this.peers.get(i).equals(getSelf())) {
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
        for(Event e : this.events)
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
    }

    public void deliver(Event e)
    {
        System.out.println("Received event "+e.id.toString());
    }

    // Default variables for the actor
    public Random rng;
    public int nodeId;
    public List<ActorRef> peers;

    // Adaptive Gossip Variables
    private int MAX_BUFFER_SIZE = 100; // Max number of messages
    public List<Event> events; // Buffer for messages
    public int minBuffer; // Minimal size of the buffer
    public int s; //Current period
    public int T;
    public int k; // Maximum age for events
    public int f; // Total number of random peers
}

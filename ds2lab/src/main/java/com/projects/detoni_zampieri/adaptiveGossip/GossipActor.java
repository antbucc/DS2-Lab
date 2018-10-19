package com.projects.detoni_zampieri.adaptiveGossip;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GossipActor extends UntypedActor {

    public GossipActor() {

        // Initialize default variables
        this.rng = new Random();
        this.nodeId = rng.nextInt();

        this.events = new ArrayList<Message>();
        this.minBuffer = MAX_BUFFER_SIZE - this.events.size();

    }

    @Override
    public void onReceive(Object o) throws Exception {

        // Catch the list message
        if (o instanceof ListMessage)
        {
            this.peers = ((ListMessage) o).m_nodes;
        } else if (o instanceof Message) {
            onReceiveGossip((Message)o);
        } else
        {
            unhandled(o);
        }
    }

    public Event getLocalEvent(Event e)
    {
        return this.get(this.events.indexOf(e));
    }

    public void onReceiveGossip(Message gossip)
    {
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
                    e_prime.age = e.age
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
    public List<Message> events; // Buffer for messages
    public int minBuffer; // Minimal size of the buffer
    public int s; //Current period
}

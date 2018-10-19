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



        } else
        {
            unhandled(o);
        }
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

package com.projects.detoni_zampieri.adaptiveGossip;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.List;
import java.util.Random;

public class GossipActor extends UntypedActor {

    public GossipActor() {
        this.rng = new Random();
        this.nodeId = rng.nextInt();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof ListMessage)
        {
            this.peers = ((ListMessage) o).m_nodes;
        } else
        {
            unhandled(o);
        }
    }

    public Random rng;
    public int nodeId;
    public List<ActorRef> peers;
}

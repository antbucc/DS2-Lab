package com.projects.detoni_zampieri.consensus.messages;

import akka.actor.ActorRef;

import java.io.Serializable;
import java.util.List;

public class ListMessage implements Serializable {

    public ListMessage(List<ActorRef> nodes) {
        this.peers = nodes;
    }

    public List<ActorRef> peers;
}

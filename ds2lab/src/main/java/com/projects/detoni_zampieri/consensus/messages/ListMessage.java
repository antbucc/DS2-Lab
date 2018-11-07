package com.projects.detoni_zampieri.consensus.messages;

import akka.actor.ActorRef;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMessage implements Serializable {

    public ListMessage(List<ActorRef> nodes,Map<ActorRef, Integer> map) {
        this.peers = nodes;
        this.actorToID = map;
    }

    public List<ActorRef> peers;
    public Map<ActorRef,Integer> actorToID;
}
